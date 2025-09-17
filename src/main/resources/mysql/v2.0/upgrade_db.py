#!/usr/bin/env python3
import pymysql
import json
import os

SQL_FILE = "update_sql.sql"
# ===================== 读取配置 =====================
CONFIG_FILE = "config.json"

if not os.path.exists(CONFIG_FILE):
    print(f"配置文件 {CONFIG_FILE} 不存在！")
    exit(1)

with open(CONFIG_FILE, "r", encoding="utf-8") as f:
    DB_CONFIG = json.load(f)

# ===================== 工具函数 =====================
def table_exists(cursor, table):
    cursor.execute("""
        SELECT COUNT(*)
        FROM information_schema.TABLES
        WHERE TABLE_SCHEMA=%s AND TABLE_NAME=%s
    """, (DB_CONFIG['database'], table))
    return cursor.fetchone()[0] > 0

def execute_sql(cursor, sql, desc=""):
    print(f"执行: {desc}")
    cursor.execute(sql)

# ===================== 检查版本 =====================
def check_update():
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()
    try:
        # 如果 easyads_version 表不存在，需要升级
        if not table_exists(cursor, "easyads_version"):
            print("easyads_version 表不存在，需要执行升级")
            return True

        cursor.execute("SELECT version FROM easyads_version ORDER BY id DESC LIMIT 1;")
        row = cursor.fetchone()
        if row is None:
            print("easyads_version 表没有数据，需要执行升级")
            return True

        current_version = row[0]
        print(f"当前数据库版本: {current_version}")

        # 字符串版本号转换为数字
        version_num = float(str(current_version).lstrip('v'))
        if version_num >= 2.0:
            print("版本 >= 2.0，无需升级")
            return False
        else:
            print("版本 < 2.0，需要升级")
            return True
    except Exception as e:
        print("检查版本失败:", e)
        return True
    finally:
        cursor.close()
        conn.close()

# ===================== 检查版本 =====================
def mysql_update():
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()
    with open(SQL_FILE, "r", encoding="utf-8") as f:
        sql_content = f.read()
    try:
        for result in cursor.execute(sql_content, multi=True):
            # 可以处理返回结果或忽略
            pass
        conn.commit()
        print("SQL 文件一次性执行完成！")
        return True
    except Exception as e:
        conn.rollback()
        print("升级失败:", e)
        return False
    finally:
        cursor.close()
        conn.close()

# ===================== 数据更新函数 =====================
def update_sdk_group():
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()
    try:
        # 查询所有 sdk_group_targeting_percentage_id 为 0 的记录
        cursor.execute("SELECT id FROM sdk_group WHERE sdk_group_targeting_percentage_id = 0")
        rows = cursor.fetchall()

        BATCH_SIZE = 2000
        total = len(rows)

        for i in range(0, total, BATCH_SIZE):
            batch = rows[i:i+BATCH_SIZE]

            # 1. 批量插入 sdk_targeting_percentage
            insert_values = [("A", 100.0)] * len(batch)
            insert_sql = "INSERT INTO sdk_targeting_percentage (tag, percentage) VALUES (%s, %s)"
            cursor.executemany(insert_sql, insert_values)

            # 2. 获取起始自增ID
            start_id = cursor.lastrowid
            inserted_ids = list(range(start_id, start_id + len(batch)))

            # 3. 构造批量 UPDATE
            update_values = list(zip(inserted_ids, [row[0] for row in batch]))
            update_sql = "UPDATE sdk_group SET sdk_group_targeting_percentage_id = %s WHERE id = %s"
            cursor.executemany(update_sql, update_values)

            conn.commit()
            print(f"Processed batch {i} ~ {i + len(batch) - 1}")
    except Exception as e:
        conn.rollback()
        print("升级失败:", e)
    finally:
        cursor.close()
        conn.close()


def update_sdk_experiment():
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()
    try:
        # 获取所有正在做AB实验的数据
        sql = """
                SELECT
                    B.id AS adspot_id, B.adspot_name
                FROM
                (
                    SELECT adspot_id, count(*) as cnt 
                    FROM sdk_group
                    GROUP BY adspot_id
                    HAVING cnt > 1
                ) A JOIN adspot B ON A.adspot_id = B.id
                JOIN media C ON B.media_id = C.id
                WHERE C.mark_delete = 0 AND B.mark_delete = 0
            """
        cursor.execute(sql)
        rows = cursor.fetchall()

        # 对每个广告位正在做AB实验的数据，给他补齐新的实验的信息
        for row in rows:
            adspot_id = row[0]
            adspot_name = row[1]
            ab_tag_sql = "INSERT INTO sdk_experiment (adspot_id, experiment_type, experiment_name, created_at, status) VALUES ('%d', 1, '%s', NOW(), 1)" % (adspot_id, adspot_name + "-AB Test")
            cursor.execute(ab_tag_sql)
            experiment_id = cursor.lastrowid

            sdk_group_sql = """
                SELECT sdk_group_percentage_id
                FROM sdk_group
                WHERE adspot_id = %s
            """ % (adspot_id)

            cursor.execute(sdk_group_sql)
            sdk_groups = cursor.fetchall()

            update_percentage_sql = """
                            UPDATE sdk_group_percentage
                            SET exp_id = %s
                            WHERE id in (%s)
                        """ % (experiment_id, ','.join(str(sg[0]) for sg in sdk_groups))
            cursor.execute(update_percentage_sql)
            conn.commit()
    except Exception as e:
        conn.rollback()
        print("升级失败:", e)
    finally:
        cursor.close()
        conn.close()

def data_update():
    # 更新 easyads.sdk_group和sdk_targeting_percentage 表
    update_sdk_group()
    # 更新 easyads.sdk_experiment数据
    update_sdk_experiment()


# ===================== 主程序 =====================
UPGRADE_STEPS = [mysql_update, data_update]

def main():
    try:
        if check_update() is False:
            print("无需升级，退出")
            return

        for step in UPGRADE_STEPS:
            if False == step(cursor):
                print("升级失败，请检查错误日志")
                return
    except Exception as e:
        print("升级失败:", e)

if __name__ == "__main__":
    main()

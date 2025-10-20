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
            print("easyads_version 表不存在")
            return -1

        cursor.execute("SELECT version FROM easyads_version ORDER BY id DESC LIMIT 1;")
        row = cursor.fetchone()
        if row is None:
            print("easyads_version 表没有数据，需要从2.0版本执行升级")
            return -1

        current_version = row[0]
        print(f"当前数据库版本: {current_version}")

        # 字符串版本号转换为数字
        version_num = float(str(current_version).lstrip('v'))
        if version_num >= 2.1:
            print("版本 >= 2.1，无需升级")
            return 0
        else:
            print("版本 < 2.1，需要升级")
            return 1
    except Exception as e:
        print("检查版本失败:", e)
        return -1
    finally:
        cursor.close()
        conn.close()

# ===================== 更新mysql数据库 =====================
def run_sql_file(cursor, path):
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    # 去掉注释
    lines = [line for line in content.splitlines() if not line.strip().startswith('--')]
    statements = [s.strip() for s in '\n'.join(lines).split(';') if s.strip()]
    for stmt in statements:
        cursor.execute(stmt)


def mysql_update():
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()

    try:
        run_sql_file(cursor, SQL_FILE)
        conn.commit()
        print("SQL 文件一次性执行完成！")
        return True
    except Exception as e:
        conn.rollback()
        print("数据库更新升级失败:", e)
        return False
    finally:
        cursor.close()
        conn.close()


# ===================== 主程序 =====================
UPGRADE_STEPS = [mysql_update]

def main():
    try:
        update_cheker = check_update()

        if -1 == update_cheker:
            print("检查升级失败，请先升级到2.0版本")
            return
        elif 0 == update_cheker:
            print("无需升级")
            return
        else:
            for step in UPGRADE_STEPS:
                if False == step():
                    print("升级失败，请检查错误日志")
                    return
                print("升级成功！")

    except Exception as e:
        print("升级失败:", e)

if __name__ == "__main__":
    main()

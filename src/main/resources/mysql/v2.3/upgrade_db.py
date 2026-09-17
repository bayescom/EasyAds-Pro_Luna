#!/usr/bin/env python3
import pymysql
import json
import os

# ===================== 读取配置 =====================
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
CONFIG_FILE = os.path.join(BASE_DIR, "config.json")
TARGET_VERSION = 2.3
TARGET_VERSION_STR = "v2.3"

# 按版本顺序的增量 SQL（相对 mysql 目录）
# 运行 v2.3 升级时，会自动补齐当前版本到 2.3 之间缺失的步骤
UPGRADE_STEPS = [
    (2.1, os.path.join(BASE_DIR, "..", "v2.1", "update_sql.sql")),
    (2.2, os.path.join(BASE_DIR, "..", "v2.2", "update_sql.sql")),
    (2.3, os.path.join(BASE_DIR, "update_sql.sql")),
]

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

def run_sql_file(cursor, path):
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    # 去掉行注释（-- 开头），保留 /* */ 块注释中的 SQL 外内容由分号切分
    lines = [line for line in content.splitlines() if not line.strip().startswith('--')]
    statements = [s.strip() for s in '\n'.join(lines).split(';') if s.strip()]
    for stmt in statements:
        # 跳过纯块注释
        if stmt.startswith('/*') and stmt.endswith('*/'):
            continue
        cursor.execute(stmt)

def get_current_version():
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()
    try:
        if not table_exists(cursor, "easyads_version"):
            print("easyads_version 表不存在，请先升级到 2.0 版本")
            return None

        cursor.execute("SELECT version FROM easyads_version ORDER BY id DESC LIMIT 1;")
        row = cursor.fetchone()
        if row is None:
            print("easyads_version 表没有数据，请先升级到 2.0 版本")
            return None

        current_version = row[0]
        version_num = float(str(current_version).lstrip('v'))
        print(f"当前数据库版本: {current_version}")
        return version_num
    except Exception as e:
        print("检查版本失败:", e)
        return None
    finally:
        cursor.close()
        conn.close()

def update_version(cursor, version_str):
    cursor.execute("INSERT INTO easyads_version (version) VALUES (%s);", (version_str,))

def mysql_update(current_version):
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()

    try:
        applied = False
        for step_version, sql_path in UPGRADE_STEPS:
            if current_version < step_version:
                abs_path = os.path.abspath(sql_path)
                if not os.path.exists(abs_path):
                    print(f"升级 SQL 不存在: {abs_path}")
                    conn.rollback()
                    return False
                print(f"执行升级到 v{step_version}: {abs_path}")
                run_sql_file(cursor, abs_path)
                applied = True

        if not applied:
            print("无需执行增量 SQL")
        else:
            update_version(cursor, TARGET_VERSION_STR)
            conn.commit()
            print(f"升级完成，当前版本已更新为 {TARGET_VERSION_STR}")
        return True
    except Exception as e:
        conn.rollback()
        print("数据库更新升级失败:", e)
        return False
    finally:
        cursor.close()
        conn.close()

# ===================== 主程序 =====================
def main():
    try:
        current_version = get_current_version()
        if current_version is None:
            print("检查升级失败，请先升级到 2.0 版本")
            return

        if current_version >= TARGET_VERSION:
            print(f"版本 >= {TARGET_VERSION_STR}，无需升级")
            return

        print(f"将从 v{current_version} 一步升级到 {TARGET_VERSION_STR}")
        if not mysql_update(current_version):
            print("升级失败，请检查错误日志")
            return
        print("升级成功！")
    except Exception as e:
        print("升级失败:", e)

if __name__ == "__main__":
    main()

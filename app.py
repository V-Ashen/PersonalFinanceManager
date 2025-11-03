from flask import Flask, request, jsonify
import oracledb
import os
from datetime import datetime

# --- IMPORTANT: Set up Oracle Thick Client if needed ---
# If you get a "DPI-1047: Cannot locate a 64-bit Oracle Client library" error,
# 1. Download the Oracle Instant Client for your OS.
# 2. Unzip it to a simple path (e.g., C:\oracle\instantclient_21_9).
# 3. Uncomment and edit the line below to point to that path.
# oracledb.init_oracle_client(lib_dir=r"C:\oracle\instantclient_21_9")

app = Flask(__name__)

# --- REPLACE WITH YOUR ORACLE DB CREDENTIALS ---
DB_USER = "SHEN"
DB_PASSWORD = "shen123"
DB_DSN = "localhost:1521/XEPDB1"

@app.route('/sync/expenses', methods=['POST'])
def sync_expenses():
    expenses_data = request.get_json()
    if not expenses_data:
        return jsonify({"status": "error", "message": "No data received"}), 400

    try:
        with oracledb.connect(user=DB_USER, password=DB_PASSWORD, dsn=DB_DSN) as connection:
            with connection.cursor() as cursor:
                p_expense_date = cursor.var(oracledb.DB_TYPE_DATE)
                p_last_modified = cursor.var(oracledb.DB_TYPE_TIMESTAMP)

                for expense in expenses_data:
                    # Parse the "YYYY-MM-DD" string for expenseDate
                    p_expense_date.setvalue(0, datetime.strptime(expense['expenseDate'], '%Y-%-m-%-d'))
                    
                    # Parse the numeric timestamp string for lastModified
                    ts_epoch = float(expense['lastModified']) / 1000 # Convert from ms to seconds
                    p_last_modified.setvalue(0, datetime.fromtimestamp(ts_epoch))
                    
                    # Call the procedure
                    cursor.callproc("MERGE_EXPENSE", [
                       expense['expenseId'],
                       expense['userId'],
                       expense['categoryId'],
                       expense['amount'],
                       p_expense_date,
                       expense['description'],
                       p_last_modified
                    ])

                print(f"Successfully processed {len(expenses_data)} records.")
        return jsonify({"status": "success", "message": "Sync complete"}), 200

    except oracledb.Error as e:
        print("Database Error:", e)
        return jsonify({"status": "error", "message": "Database operation failed"}), 500
    except Exception as e:
        print("An error occurred:", e)
        return jsonify({"status": "error", "message": "An unexpected server error occurred"}), 500

if __name__ == '__main__':
    # This runs the Flask web server when you execute "python app.py"
    # debug=True allows the server to automatically reload when you save changes
    app.run(host='0.0.0.0', debug=True)
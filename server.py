from flask import Flask, request, jsonify
import pyperclip

app = Flask(__name__)

clipboard_text = ""
last_update = 0

@app.route("/push", methods=["POST"])
def push():
    global clipboard_text, last_update
    data = request.json.get("text", "")
    clipboard_text = data
    pyperclip.copy(clipboard_text)
    last_update += 1
    return jsonify({"status": "ok", "updated": last_update})

@app.route("/pull", methods=["GET"])
def pull():
    global clipboard_text, last_update
    return jsonify({"text": clipboard_text, "updated": last_update})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=3423)

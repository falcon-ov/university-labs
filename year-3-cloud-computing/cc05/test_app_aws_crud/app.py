from flask import Flask, request, jsonify
from dynamodb_crud import create_todo, get_todos, update_todo, delete_todo

app = Flask(__name__)

@app.route('/dynamo/todos', methods=['GET'])
def list_todos():
    todos = get_todos()
    return jsonify(todos)

@app.route('/dynamo/todos', methods=['POST'])
def add_todo():
    data = request.json
    create_todo(data)
    return jsonify({"message": "Todo created"}), 201

@app.route('/dynamo/todos/<todo_id>', methods=['PUT'])
def edit_todo(todo_id):
    data = request.json
    update_todo(todo_id, data)
    return jsonify({"message": "Todo updated"})

@app.route('/dynamo/todos/<todo_id>', methods=['DELETE'])
def remove_todo(todo_id):
    delete_todo(todo_id)
    return jsonify({"message": "Todo deleted"})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001)

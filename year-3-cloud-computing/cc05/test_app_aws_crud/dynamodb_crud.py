import boto3
from boto3.dynamodb.conditions import Key

dynamodb = boto3.resource('dynamodb', region_name='eu-central-1')
table = dynamodb.Table('Todos')

def create_todo(todo):
    table.put_item(Item=todo)

def get_todos():
    response = table.scan()
    return response.get('Items', [])

def update_todo(todo_id, update_fields):
    update_expr = "SET " + ", ".join(f"{k}=:{k}" for k in update_fields)
    expr_attr_vals = {f":{k}": v for k, v in update_fields.items()}
    table.update_item(
        Key={'todo_id': todo_id},
        UpdateExpression=update_expr,
        ExpressionAttributeValues=expr_attr_vals
    )

def delete_todo(todo_id):
    table.delete_item(Key={'todo_id': todo_id})

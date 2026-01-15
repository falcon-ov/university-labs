function fetchTodos() {
    fetch('/todos')
        .then(response => response.json())
        .then(data => {
            const list = document.getElementById('todo-list');
            list.innerHTML = '';
            data.forEach(todo => {
                const li = document.createElement('li');
                li.textContent = `ID: ${todo.id}, Title: ${todo.title}, Category: ${todo.category_id}, Status: ${todo.status}`;
                li.onclick = () => deleteTodo(todo.id);
                list.appendChild(li);
            });
        });
}

function addTodo() {
    const title = document.getElementById('todo-title').value;
    const categoryId = parseInt(document.getElementById('category-id').value);

    fetch('/todos', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({title: title, category_id: categoryId, status: 'pending'})
    }).then(() => {
        fetchTodos();
    });
}

function deleteTodo(id) {
    fetch(`/todos/${id}`, { method: 'DELETE' })
        .then(() => fetchTodos());
}

// загрузка задач при открытии страницы
window.onload = fetchTodos;

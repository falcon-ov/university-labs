document.addEventListener("DOMContentLoaded", function () {
    let stepContainer = document.getElementById("steps-list");
    let addStepBtn = document.getElementById("add-step");

    addStepBtn.addEventListener("click", function () {
        let stepDiv = document.createElement("div");
        stepDiv.classList.add("step");

        let removeBtn = document.createElement("span");
        removeBtn.textContent = "❌";
        removeBtn.classList.add("remove-btn");
        removeBtn.addEventListener("click", function () {
            stepDiv.remove();
        });

        let input = document.createElement("input");
        input.type = "text";
        input.name = "steps[]";
        input.placeholder = "Введите шаг";

        // Сначала добавляем кнопку, потом поле ввода
        stepDiv.appendChild(removeBtn);
        stepDiv.appendChild(input);
        stepContainer.appendChild(stepDiv);
    });

    // Обрабатываем уже существующие кнопки удаления (если они есть в HTML)
    document.querySelectorAll(".remove-btn").forEach(button => {
        button.addEventListener("click", function () {
            this.parentElement.remove();
        });
    });
});
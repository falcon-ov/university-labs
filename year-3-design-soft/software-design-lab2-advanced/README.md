# Лабораторная работа 2

- Тема: **Pipelines**.
- Выполнил: **Даниил Соколов**
- Группа: **I2302(ru)**
## Концепты

- Абстракция (ссылка на код по имени)
- Полиморфизм (интерфейс)
- Контекст
- Паттерн Адаптер
- Паттерн Strategy (передача действия параметром)
- Интроспекция
- Data-Oriented (манипуляция операций как с данными)
- Паттерн Декоратор (Wrapper)

## Задания

1. Создать свой pipeline, как в видео, но на свою тематику. ☑
2. Сконфигурируйте тестовый pipeline в основной функции с демонстрацией использования. ☑

### Какие паттерны я использовал

**Pipeline (конвейер)**  
Я разбил обработку на последовательные шаги, которые можно было легко менять местами, добавлять или убирать. Это обеспечило гибкость архитектуры.\
**Chain of Responsibility (цепочка обязанностей)**  
На каждом этапе я принимал решение: «Обработал — дальше не идём». Для этого я внедрил флаг `isDone` в контексте, чтобы остановить цепочку при необходимости.
```java
package org.example.core;


import java.util.ArrayList;
import java.util.List;

public class Pipeline<TContext extends HasDoneFlag> {
    private final List<IPipelineStep<TContext>> steps = new ArrayList<>();

    public Pipeline<TContext> add(IPipelineStep<TContext> step) {
        steps.add(step);
        return this;
    }

    public void execute(TContext context) {
        for (IPipelineStep<TContext> step : steps) {
            if (context.isDone()) break; // Chain of Responsibility
            step.execute(context);
        }
    }

    public void describe(StringBuilder sb) {
        sb.append("Pipeline:\n");
        for (IPipelineStep<TContext> step : steps) {
            step.describe(sb);
        }
    }
    public void printDescription() {
        StringBuilder sb = new StringBuilder();
        describe(sb);
        System.out.print(sb.toString());
    }

}
```

**Decorator (декоратор)**  
Я обернул шаги в дополнительные классы — такие как логирование и тайминг — не меняя их внутренний код. Так я добавил функциональность извне, сохранив чистоту реализации.
```java
package org.example.core;

public abstract class StepDecorator<TContext> implements IPipelineStep<TContext> {
    protected final IPipelineStep<TContext> inner;

    public StepDecorator(IPipelineStep<TContext> inner) {
        this.inner = inner;
    }

    @Override
    public void execute(TContext ctx) {
        before(ctx);
        inner.execute(ctx);
        after(ctx);
    }

    protected void before(TContext ctx) {}
    protected void after(TContext ctx) {}
}
```

**Strategy (стратегия)**  
Каждый шаг я реализовал как отдельную стратегию обработки. Я передал их в пайплайн как параметры и выполнил в нужной последовательности.

**Context (контекст)**  
Я использовал единый объект `OrderContext`, в котором хранил состояние заказа: список товаров, сумму, статус и другие данные, необходимые для обработки.
```java
package org.example.context;

import org.example.core.HasDoneFlag;
import java.util.List;

public class OrderContext implements HasDoneFlag {
    private boolean done;
    private List<String> items;
    private double total;
    private boolean approved;

    public OrderContext(List<String> items) {
        this.items = items;
    }

    public List<String> getItems() { return items; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    @Override public boolean isDone() { return done; }
    @Override public void setDone(boolean done) { this.done = done; }
}
```

### **Дальнейшие задания (как минимум 4 на выбор)**:

- Используйте generics чтобы задавать тип контекста извне.
  Сделайте `Pipeline` и `IPipelineStep` зависимыми от типа контекста
  (то есть, `Pipeline<TContext>` и `IPipelineStep<TContext>`). \
  Используйте 2 разных вида контекста при демонстрации. ☑
  ```java
  package org.example.core;
  
  public interface IPipelineStep<TContext> {
      void execute(TContext context);
      //...
  ```
  ```java
  package org.example.core;
  //...
  public class Pipeline<TContext extends HasDoneFlag> {
      private final List<IPipelineStep<TContext>> steps = new ArrayList<>();
      //...
  ```
  ```java

- Улучшите систему интроспекции.
  Например, пусть метод принимает `StringBuilder`, и добавляет свое описание туда.\
  Сделайте так же вспомогательные функции на основе этой новой системы.
  Например, функция для печати всех шагов.
  `Без паттерна Visitor`
  ```java
  package org.example;
  
  //...
  
  public class Main {
      public static void main(String[] args) {
          
          ///...
  
          System.out.println();
  
          System.out.println("=== Состав пайплайна заказа ===");
          orderPipeline.printDescription();
  
          System.out.println();
  
          System.out.println("=== Состав пайплайна оплаты ===");
          paymentPipeline.printDescription();
      }
  }
  ```
  Переопределил describe() в каждом Step...
  ```java
  package org.example.steps.order;
  //...
  
  public class CartValidationStep implements IPipelineStep<OrderContext> {
      
      //...
      @Override
      public void describe(StringBuilder sb) {
          sb.append("CartValidationStep: проверяет, что корзина не пуста\n");
      }
  }
  ```
  И в Pipeline добавил printDescription()...
  ```java
  package org.example.core;
  
  //...
  
  public class Pipeline<TContext extends HasDoneFlag> {
      private final List<IPipelineStep<TContext>> steps = new ArrayList<>();
  
      //...
  
      public void describe(StringBuilder sb) {
          sb.append("Pipeline:\n");
          for (IPipelineStep<TContext> step : steps) {
              step.describe(sb);
          }
      }
      public void printDescription() {
          StringBuilder sb = new StringBuilder();
          describe(sb);
          System.out.print(sb.toString());
      }
  
  }
  ```

- Используйте паттерн Декоратор, чтобы создать хотя бы 2 `PipelineStep`-а, которые подойдут вашей тематике.
  Выполняйте код до, после или и до и после шага, сохраненном в них.

  ```java
  package org.example.decorators;
  
  
  import org.example.core.IPipelineStep;
  import org.example.core.StepDecorator;
  
  public class TimingDecorator<TContext> extends StepDecorator<TContext> {
      private long start;
      public TimingDecorator(IPipelineStep<TContext> inner) { super(inner); }
  
      @Override protected void before(TContext ctx) { start = System.nanoTime(); }
      @Override protected void after(TContext ctx) {
          long ms = (System.nanoTime() - start) / 1_000_000;
          System.out.println("Время выполнения: " + ms + " ms");
      }
  }
  ```
  ```java
  package org.example.decorators;
  
  
  import org.example.core.IPipelineStep;
  import org.example.core.StepDecorator;
  
  public class TimingDecorator<TContext> extends StepDecorator<TContext> {
      private long start;
      public TimingDecorator(IPipelineStep<TContext> inner) { super(inner); }
  
      @Override protected void before(TContext ctx) { start = System.nanoTime(); }
      @Override protected void after(TContext ctx) {
          long ms = (System.nanoTime() - start) / 1_000_000;
          System.out.println("Время выполнения: " + ms + " ms");
      }
  }
  
  ```

- Сделайте возможным работу вашего pipeline-а как Responsibility Chain
  (отдельные шаги могут решить завершить выполнение последующих шагов).
  Для этого, можете хранить в контексте поле `IsDone`.
  Не выполняйте следующий этап, если это поле выставлено.

  ```java
  package org.example.core;
  
  public interface HasDoneFlag {
      boolean isDone();
      void setDone(boolean done);
  }
  ```
  ```java
  package org.example.core;

  //...  

  public class Pipeline<TContext extends HasDoneFlag> {
      private final List<IPipelineStep<TContext>> steps = new ArrayList<>();
  
      public Pipeline<TContext> add(IPipelineStep<TContext> step) {
          steps.add(step);
          return this;
      }
      public void execute(TContext context) {
          for (IPipelineStep<TContext> step : steps) {
              if (context.isDone()) break; // Chain of Responsibility
              step.execute(context);
          }
      }
      //...
  ```
  
## Вывод
В лабораторной работе я реализовал собственный Pipeline для обработки заказов и оплаты в интернет‑магазине.

Архитектура построена на Generics, что позволило использовать разные контексты (OrderContext, PaymentContext).

Добавлены ключевые паттерны: Chain of Responsibility (флаг isDone), Decorator (логирование и тайминг шагов), Strategy (каждый шаг как отдельная стратегия), Context (единый объект состояния).

Система поддерживает интроспекцию: каждый шаг умеет описывать себя, а пайплайн печатает полный состав.

В main продемонстрирована работа двух пайплайнов с разными сценариями.

Таким образом, цели лабораторной достигнуты: показана гибкая и расширяемая архитектура, где бизнес‑логика разделена на независимые шаги, которые легко комбинировать и модифицировать.
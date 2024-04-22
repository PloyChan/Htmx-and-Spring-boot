package com.example.demo;

import io.github.wimdeblauwe.hsbt.mvc.HtmxResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;

@SpringBootApplication
public class HtmxApplication {

    public static void main(String[] args) {
        SpringApplication.run(HtmxApplication.class, args);
    }

}


@Component
class initialize {

    private final TodoRepository todoRepository;

    initialize(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    void reset() {
        this.todoRepository.deleteAll();
        Stream.of("Learn HTMX",
                "Learn String ViewComponent",
                "Learn Hotwire",
                "Make some Coffee")
                .forEach(t -> this.todoRepository.save(new Todo(null,t)));
    }
}

@RequestMapping(value = "/todos")
@Controller
class TodoController {
    private final TodoRepository todoRepository;

    TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping
    String todos(Model model) {
        model.addAttribute("todos",this.todoRepository.findAll());
        return "todos";
    }

//    @PostMapping
//    HtmxResponse add(@RequestParam("new-todo") String title, Model model) {
//        this.todoRepository.save(new Todo(null, title));
//        model.addAttribute("todos",this.todoRepository.findAll());
//        return new HtmxResponse()
//                .addTemplate("todos :: todos-list");
//    }
    @PostMapping
    String add(@RequestParam("new-todo") String title, Model model) {
        this.todoRepository.save(new Todo(null, title));
        model.addAttribute("todos",this.todoRepository.findAll());
        return  "todos :: todos-list";
    }
    @ResponseBody
    @DeleteMapping(value = "/{id}", produces = MediaType.TEXT_HTML_VALUE)
    String delete(@PathVariable Integer id) {
        System.out.println("going to delete id: "+id);
        this.todoRepository.deleteById(id);
        return "";
    }
}

interface TodoRepository extends CrudRepository<Todo, Integer> {

}

record Todo(@Id Integer id, String title) {

}

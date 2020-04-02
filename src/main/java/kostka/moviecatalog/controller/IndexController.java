package kostka.moviecatalog.controller;

import kostka.moviecatalog.service.rabbitmq.RabbitMqSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/")
public class IndexController {
    private RabbitMqSender rabbitMqSender;

    @Autowired
    public IndexController(final RabbitMqSender rabbitMqSender) {
        this.rabbitMqSender = rabbitMqSender;
    }

    @GetMapping
    public String index() {
        rabbitMqSender.sendToAllMoviesQueue();
        rabbitMqSender.sendToLatestMoviesQueue();
        rabbitMqSender.sendToRatingQueue();
        return "index";
    }
}

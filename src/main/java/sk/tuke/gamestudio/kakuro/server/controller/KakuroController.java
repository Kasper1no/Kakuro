package sk.tuke.gamestudio.kakuro.server.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

@Controller
@RequestMapping("/kakuro")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class KakuroController {

}

package sk.tuke.gamestudio.kakuro.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;
import sk.tuke.gamestudio.kakuro.core.Field;
import sk.tuke.gamestudio.kakuro.entity.DTO.UpdateFieldDTO;
import sk.tuke.gamestudio.kakuro.entity.DTO.UpdateResponseDTO;

@Controller
@RequestMapping("/kakuro")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class KakuroController {

    private Field field;
    private int multiplier;
    private int newMultiplier;

    @GetMapping("/start")
    public String startGame(@RequestParam(defaultValue = "easy") String difficulty, Model model) {
        switch (difficulty) {
            case "medium" -> multiplier = 2;
            case "hard" -> multiplier = 3;
            default -> multiplier = 1;
        };

        String returnedDifficulty = difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1).toLowerCase();

        model.addAttribute("difficulty", returnedDifficulty);
        return "kakuro";
    }

    @GetMapping("/field")
    @ResponseBody
    public Field getField() {
        System.out.println("field start");

        if (field == null || multiplier != newMultiplier) {
            try {
                field = switch (multiplier) {
                    case 2 -> {
                        newMultiplier = 2;
                        yield new Field(4, 4);
                    }
                    case 3 -> {
                        newMultiplier = 3;
                        yield new Field(5, 5);
                    }
                    default -> {
                        newMultiplier = 1;
                        yield new Field(3, 3);
                    }
                };
                if(field.getField() == null)field = null;
            } catch (Exception e) {
                System.err.println("Error generating field: " + e.getMessage());
                field = null;
            }
        }

        System.out.println("field end");

        return field;
    }

    @PostMapping("/update")
    @ResponseBody
    public UpdateResponseDTO getUpdateField(@RequestBody UpdateFieldDTO body) {
        int x = body.getX();
        int y = body.getY();
        int value = body.getValue();

        if(field.setValue(x, y, value)) {

            if (field.isSolved()) {
                int score = 10 * multiplier;
                multiplier = 0;
                newMultiplier = 0;

                return new UpdateResponseDTO(field, score);
            }

            return new UpdateResponseDTO(field, 0);
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to put value in this tile: " + x + " " + y + " " + value);
        }
    }

    @PostMapping("/reset")
    @ResponseBody
    public UpdateResponseDTO resetField() {
        field.reset();
        return new UpdateResponseDTO(field, 0);
    }

    @PostMapping("/resign")
    @ResponseBody
    public UpdateResponseDTO resignGame(){
        field.showSolution();
        newMultiplier = 0;
        multiplier = 0;
        return new UpdateResponseDTO(field, 0);
    }
}

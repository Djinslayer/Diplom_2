package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {

    private ArrayList<String> ingredients;

    public static Order getRealIngredients() {
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add("61c0c5a71d1f82001bdaaa73");
        ingredients.add("61c0c5a71d1f82001bdaaa70");
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        return new Order(ingredients);
    }

    public static Order getSomeUnrealIngredients() {
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add("60d3463f7034a000269f45e7");
        ingredients.add("60d3463f7034a000269f45e9");
        ingredients.add("60d3463f7034a000269f45e8");
        ingredients.add("60d3463f7034a000269f45ea");
        return new Order(ingredients);
    }

    public static Order getAllUnrealIngredients() {
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add("160d3463f7034a000269f45e7");
        ingredients.add("160d3463f7034a000269f45e9");
        ingredients.add("160d3463f7034a000269f45e8");
        ingredients.add("160d3463f7034a000269f45ea");
        return new Order(ingredients);
    }
}

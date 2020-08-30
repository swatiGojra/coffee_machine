package io.dunzo.coffeeMachine.request;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author swatigojra
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Beverage implements Serializable
{
  @SerializedName("name")
  private String name;

  @SerializedName("display_name")
  private String displayName;

  @SerializedName("ingredients")
  private List<IngredientRequirement> ingredientRequirementList;

}

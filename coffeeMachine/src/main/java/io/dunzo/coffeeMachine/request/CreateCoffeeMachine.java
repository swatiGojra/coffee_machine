package io.dunzo.coffeeMachine.request;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 *  @author swatigojra
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCoffeeMachine implements Serializable
{
  @SerializedName("outlets")
  private Outlets outlets;

  @SerializedName("total_items_quantity")
  private List<IngredientRequirement> totalItemsQuantityList;

  @SerializedName("beverages")
  private List<Beverage> beverageList;

}

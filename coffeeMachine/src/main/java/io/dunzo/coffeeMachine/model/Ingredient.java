package io.dunzo.coffeeMachine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  @author swatigojra
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Ingredient
{
   private String name;
   private String displayName;

   @Override
   public int hashCode() {
      return this.getName().hashCode();
   }

   @Override
   public boolean equals(Object ingredient) {
      Ingredient ingredientObj = (Ingredient) ingredient;
      return this.getName().equals(ingredientObj.getName());
   }

}

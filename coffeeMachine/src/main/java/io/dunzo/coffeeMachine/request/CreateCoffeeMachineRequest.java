/**
 * 
 */
package io.dunzo.coffeeMachine.request;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *  @author swatigojra
 *
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateCoffeeMachineRequest implements Serializable {

  @SerializedName("machine")
  private CreateCoffeeMachine createCoffeeMachine;

}

package io.dunzo.coffeeMachine.request;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author swatigojra
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Outlets implements Serializable
{
  @SerializedName("count_n")
  private Integer noOfMachines;

}

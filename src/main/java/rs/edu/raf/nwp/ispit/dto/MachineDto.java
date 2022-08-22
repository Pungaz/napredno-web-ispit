package rs.edu.raf.nwp.ispit.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class MachineDto {
    @NotBlank(message = "Name of the machine is mandatory")
    String name;
}

package mbds.car.pooling.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReviewDto {
    @Min(value = 0, message = "La note doit être au minimum 0")
    @Max(value = 5, message = "La note doit être au maximum 5")
    private Integer rating;

    private String comment;
}
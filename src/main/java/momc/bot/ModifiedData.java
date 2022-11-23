package momc.bot;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Member;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
public class ModifiedData {

    private Member lastModifiedBy;
    private LocalDateTime lastModifiedDate;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(lastModifiedBy)
                .append("\n")
                .append(lastModifiedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return sb.toString();
    }
}

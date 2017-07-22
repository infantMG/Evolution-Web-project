package evolution.model.tweet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import evolution.model.user.StandardUser;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Infant on 22.07.2017.
 */
@Entity
@Table(name = "repost")
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor @AllArgsConstructor @ToString @Getter @Setter
public class Repost {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_repost")
    @SequenceGenerator(name = "seq_repost", sequenceName = "seq_repost_id", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "tweet_id")
    private Tweet tweet;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "reposted_user_id")
    private StandardUser reposted;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty
    private Date date;

}

package evolution.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import evolution.model.user.StandardUser;
import lombok.*;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Infant on 05.07.2017.
 */
@Entity
@Table(name = "publication")
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor @AllArgsConstructor @Getter @Setter @ToString
public class Publication {

    @JsonProperty
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_publication")
    @SequenceGenerator(name = "seq_publication", sequenceName = "seq_publication_id", allocationSize = 1)
    private Long id;

    @JsonProperty
    @Column
    private String content;

    @JsonProperty
    @Column(name = "date_publication")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @JsonProperty
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    private StandardUser sender;

    @JsonProperty
    @Column(name = "category_id")
    private Long category;

    @JsonProperty
    @Column(name = "theme_publication")
    private String theme;

    @JsonProperty
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "comment",
            joinColumns = @JoinColumn(name = "publication_id") ,
            inverseJoinColumns =  @JoinColumn(name = "id"))
    @Fetch(FetchMode.SUBSELECT)
    private List<Comment> commentList;

    public Publication(Long id) {
        this.id = id;
    }
}

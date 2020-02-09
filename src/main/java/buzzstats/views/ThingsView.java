package buzzstats.views;

import buzzstats.api.Thing;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.views.View;
import java.util.List;

/** The view for the things resource. */
public class ThingsView extends View {
  private List<Thing> things;

  public ThingsView() {
    super("things.mustache");
  }

  @JsonProperty
  public List<Thing> getThings() {
    return things;
  }

  public void setThings(List<Thing> things) {
    this.things = things;
  }
}

import io.micrometer.core.instrument.Gauge;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import org.testng.annotations.Test;

import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

public class MicrometerTest {
    @Test
    public void testWrite004() throws Exception {
        CollectorRegistry registry = buildRegistry();

        StringWriter writer = new StringWriter();
        TextFormat.write004(writer, registry.metricFamilySamples());
        String output = writer.toString();
        System.out.println(output);
        assertThat(output).isEqualTo(""
                + "# HELP nolabels help\n"
                + "# TYPE nolabels histogram\n"
                + "nolabels_bucket{le=\"100\",} 0.0\n"
                + "nolabels_bucket{le=\"+Inf\",} 2.0\n"
                + "# HELP nolabels_created help\n"
                + "# TYPE nolabels_created gauge\n"
                + "nolabels_created 1234.0\n"
                + "# HELP nolabels_gcount help\n"
                + "# TYPE nolabels_gcount gauge\n"
                + "nolabels_gcount 2.0\n"
                + "# HELP nolabels_gsum help\n"
                + "# TYPE nolabels_gsum gauge\n"
                + "nolabels_gsum 7.0\n");
    }

    @Test
    public void testWriteOpenMetrics100() throws Exception {
        CollectorRegistry registry = buildRegistry();

        StringWriter writer = new StringWriter();
        TextFormat.writeOpenMetrics100(writer, registry.metricFamilySamples());
        String output = writer.toString();
        System.out.println(output);
        assertThat(output).isEqualTo(""
                + "# HELP nolabels help\n"
                + "# TYPE nolabels histogram\n"
                + "nolabels_bucket{le=\"+Inf\",} 2.0\n"
                + "# HELP nolabels_created help\n"
                + "# TYPE nolabels_created gauge\n"
                + "nolabels_created 1234.0\n"
                + "# HELP nolabels_gcount help\n"
                + "# TYPE nolabels_gcount gauge\n"
                + "nolabels_gcount 2.0\n"
                + "# HELP nolabels_gsum help\n"
                + "# TYPE nolabels_gsum gauge\n"
                + "nolabels_gsum 7.0\n");
    }

    private static CollectorRegistry buildRegistry() {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        Gauge.builder("nolabels_bucket", () -> 0.0)
             .tags("le", "100")
             .description("help")
             .register(registry);
        Gauge.builder("nolabels_bucket", () -> 2.0)
             .tags("le", "+Inf")
             .description("help")
             .register(registry);
        Gauge.builder("nolabels_gcount", () -> 2.0)
             .description("help")
             .register(registry);
        Gauge.builder("nolabels_gsum", () -> 7.0)
             .description("help")
             .register(registry);
        Gauge.builder("nolabels_created", () -> 1234.0)
             .description("help")
             .register(registry);

        return registry.getPrometheusRegistry();
    }
}

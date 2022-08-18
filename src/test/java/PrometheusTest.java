import io.prometheus.client.Collector;
import io.prometheus.client.Collector.MetricFamilySamples.Sample;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import org.testng.annotations.Test;

import java.io.StringWriter;
import java.util.List;

import static io.prometheus.client.Collector.Type.GAUGE_HISTOGRAM;
import static org.assertj.core.api.Assertions.assertThat;

public class PrometheusTest {
    @Test
    public void testWrite004() throws Exception {
        CollectorRegistry registry = buildGaugeHistogramRegistry();
        StringWriter writer = new StringWriter();
        TextFormat.write004(writer, registry.metricFamilySamples());
        assertThat(writer.toString()).isEqualTo(""
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
        CollectorRegistry registry = buildGaugeHistogramRegistry();
        StringWriter writer = new StringWriter();
        TextFormat.writeOpenMetrics100(writer, registry.metricFamilySamples());
        assertThat(writer.toString()).isEqualTo(""
                + "# TYPE nolabels gaugehistogram\n"
                + "# HELP nolabels help\n"
                + "nolabels_bucket{le=\"100\"} 0.0\n"
                + "nolabels_bucket{le=\"+Inf\"} 2.0\n"
                + "nolabels_gcount 2.0\n"
                + "nolabels_gsum 7.0\n"
                + "nolabels_created 1234.0\n"
                + "# EOF\n");
    }

    private static CollectorRegistry buildGaugeHistogramRegistry() {
        CollectorRegistry registry = new CollectorRegistry();

        class CustomCollector extends Collector {
            public List<MetricFamilySamples> collect() {
                List<String> labelNames = List.of();
                List<String> labelValues = List.of();
                return List.of(new MetricFamilySamples(
                        "nolabels",
                        GAUGE_HISTOGRAM,
                        "help",
                        List.of(new Sample("nolabels_bucket", List.of("le"), List.of("100"), 0.0),
                                new Sample("nolabels_bucket", List.of("le"), List.of("+Inf"), 2.0),
                                new Sample("nolabels_gcount", labelNames, labelValues, 2.0),
                                new Sample("nolabels_gsum", labelNames, labelValues, 7.0),
                                new Sample("nolabels_created", labelNames, labelValues, 1234.0))));
            }
        }
        new CustomCollector().register(registry);
        return registry;
    }
}

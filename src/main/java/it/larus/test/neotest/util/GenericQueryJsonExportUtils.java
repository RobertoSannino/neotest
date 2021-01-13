package it.larus.test.neotest.util;

public class GenericQueryJsonExportUtils {

    private static final String RESULT_ARRAY_WRAPPER =
            "CALL apoc.export.json.query(\"%s\", null, {stream: true, format: 'ARRAY_JSON'}) YIELD data\n" +
                    "RETURN data";

    // TODO NODES_AND_RELATIONSHIP WRAPPER

    public enum ExportMode {
        RESULT_ARRAY,
        NODES_AND_RELATIONSHIPS
    }

    private GenericQueryJsonExportUtils() {}

    public static String wrapWithExport(String query, ExportMode mode) {
        if (mode == ExportMode.RESULT_ARRAY) {
            query = query.replace("\"", "\\\\\""); // escape quotes in provided query

            return String.format(RESULT_ARRAY_WRAPPER, query);
        }

        throw new UnsupportedOperationException(ExportMode.NODES_AND_RELATIONSHIPS.name() + " not currently supported");
    }
}

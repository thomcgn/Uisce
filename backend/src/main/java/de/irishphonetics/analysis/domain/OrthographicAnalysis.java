package de.irishphonetics.analysis.domain;

import java.util.List;

public record OrthographicAnalysis(String word, List<OrthographicSegment> segments) {
    public OrthographicAnalysis {
        segments = List.copyOf(segments);
    }
}

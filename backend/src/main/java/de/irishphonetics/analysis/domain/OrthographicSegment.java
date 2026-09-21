package de.irishphonetics.analysis.domain;

import de.irishphonetics.phonology.domain.Mutation;
import de.irishphonetics.phonology.domain.Position;
import de.irishphonetics.phonology.domain.Quality;
import java.util.Optional;

public record OrthographicSegment(String grapheme, Position position,
                                  Optional<Quality> quality, Mutation mutation) {
}

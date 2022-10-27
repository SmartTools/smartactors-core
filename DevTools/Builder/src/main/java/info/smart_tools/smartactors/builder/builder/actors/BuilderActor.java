package info.smart_tools.smartactors.builder.builder.actors;

import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;
import info.smart_tools.smartactors.builder.feature_builder.IFeatureBuilder;

public class BuilderActor extends StatelessActor {

    private final IFeatureBuilder featureBuilder;

    public BuilderActor(final IFeatureBuilder featureBuilder) {
        this.featureBuilder = featureBuilder;
    }

    @Executable
    public void buildFeature(final String pathToFeature) {
        this.featureBuilder.build(pathToFeature);
    }
}

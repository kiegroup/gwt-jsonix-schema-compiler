GWT-JSONIX-COMPILER
===================

![Build Status](https://github.com/kiegroup/gwt-jsonix-schema-compiler/actions/workflows/gwt-jsonix-schema-compiler-build.yml/badge.svg)

Utility built around JSONIX tools to create JSInterop classes from an xsd schema and use them for
client-side marshalling/unmarshalling.

Custom parameter for plugin
---------------------------

    -Xgwtjsonix // enable the gwt-jsonix plugin extension
    -Xinheritance // enable jaxb inheritance
    -Xnamespace-prefix // enable jaxb namespace prefix
    -jsid=${path_to_build_directory}
    -jsmpkg=${name_of_mapper_package}
    -jsmn=${name_of_custom_mainjs} (default = "MainJs")

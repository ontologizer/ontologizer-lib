.. _index:

=====================
OntologizerLib Manual
=====================

.. note:: **This documents somewhat "into the future" in terms of availability on Maven Central.**

OntologizerLib is a Java library with data structures for working with ontologies and routines for I/O of OBO files.


--------------------
Use in Maven Project
--------------------

You can use the following XML fragment for usage with Maven

.. code-block:: xml

    <!-- https://mvnrepository.com/artifact/de.ontologizer/ontologizerlib-core -->
    <dependency>
        <groupId>de.ontologizer</groupId>
        <artifactId>ontologizerlib-core</artifactId>
        <version>0.1</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/de.ontologizer/ontologizerlib-io -->
    <dependency>
        <groupId>de.ontologizer</groupId>
        <artifactId>ontologizerlib-io</artifactId>
        <version>0.1</version>
    </dependency>


------------
Java Example
------------

Here is, in short, how to load an OBO file into an ``Ontology`` object.

.. code-block:: java

    // Parse file and construct TermContainer
    String pathObo = "path/to/file.obo";
    OBOParser parser = new OBOParser(new OBOParserFileInput(pathObo));
    parser.doParse();
    TermContainer termContainer =
            new TermContainer(parser.getTermMap(),
                              parser.getFormatVersion(),
                              parser.getDate());
    // Create Ontology object from this
    Ontology ontology = Ontology.create(termContainer);


------------------
External Resources
------------------

Here are some more resources besides this manual:

- `Github Project <https://github.com/ontologizer/OntologizerLib>`_
- API Javadoc
    - basic data structures are located in `ontologizerlib-core <http://javadoc.io/doc/de.ontologizer/ontologizerlib-core/0.20>`_
    - I/O code can be found in `ontologizerlib-io <http://javadoc.io/doc/de.ontologizer/ontologizerlib-io/0.20>`_


--------------
Project Status
--------------

OntologizerLib is currently under active development and being refactored.
However, we are successfully using OntologizerLib in a number of our projects.


.. toctree::
    :caption: OntologizerLib Usage
    :name: ontologizerlib-usage
    :maxdepth: 1
    :hidden:

    quickstart


.. toctree::
    :caption: Project Info
    :name: project-info
    :maxdepth: 1
    :hidden:

    contributing
    authors
    history
    license

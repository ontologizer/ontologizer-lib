.. _quickstart:

==========
Quickstart
==========

As OntologizerLib is available from Maven central, there is no need for installation.
You can simply plug the following snippet into your ``pom.xml`` file and you are ready to go.
Of course, you don't have to use ``ontologizerlib-io`` but in most cases, you will want to use it.

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

-----------------
Running the Demos
-----------------

There are two demo applications in the ``demos`` sub directory.
To build and run them, you have to have Git and Maven installed.
Then, you can run the commands from below.

First, clone the repository to your local installation

.. code-block:: shell

    $ git clone -n 1 git://github.com/ontologizer/OntologizerLib.git

OBO to DOT Conversion
=====================

The ``obo2dot`` demo application reads in an OBO file and dumps the contained ontology into the DOT format.
DOT files can be rendered to images using `GraphViz <http://www.graphviz.org/>`_.

.. code-block:: shell

    $ cd OntologizerLib/demos/obo2dot
    $ mvn clean package
    [...]
    $ java -jar target/obo2dot-*.jar files/gene_ontology.1_2.obo.gz out.dot
    [...]
    $ head out.dot
    /* Generated with OntologizerLib 0.1-SNAPSHOT */
    digraph G {nodesep=0.4;
    GO_0000000;
    GO_0000001;
    GO_0000002;
    GO_0000003;
    GO_0000005;
    GO_0000006;
    GO_0000007;
    GO_0000008;


Compute Information Content
===========================

.. code-block:: shell

    $ cd OntologizerLib/demos/obo2ic
    $ mvn clean package
    [...]
    $ java -jar target/obo2ic-*.jar files/gene_ontology.1_2.obo.gz \
        files/gene_association.sgd.gz out.tsv
    [...]
    $ head out.tsv
    GO:0000001  5.0802946101263675
    GO:0000002  4.88613859568541
    GO:0000006  8.412499120301572
    GO:0000007  8.412499120301572
    GO:0000009  6.333057578621736
    GO:0000010  8.412499120301572
    GO:0000011  5.522127362405407
    GO:0000014  6.466588971246258
    GO:0000015  7.313886831633462
    GO:0000017  7.026204759181681

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

    <!-- https://mvnrepository.com/artifact/de.ontologizer/OntologizerLib -->
    <dependency>
        <groupId>de.ontologizer</groupId>
        <artifactId>OntologizerLib</artifactId>
        <version>0.1</version>
    </dependency>


------------
Java Example
------------

Here is, in short, how to load an OBO file and iterate over it.

.. code-block:: java

    /** TODO */


------------------
External Resources
------------------

Here are some more resources besides this manual:

- `Github Project <https://github.com/ontologizer/OntologizerLib>`_
- API Javadoc
    - `ontologizerlib-core <http://javadoc.io/doc/de.ontologizer/ontologizerlib-core/0.20>`_
    - `ontologizerlib-io <http://javadoc.io/doc/de.ontologizer/ontologizerlib-io/0.20>`_


.. toctree::
    :caption: Installation & Getting Started
    :name: getting-started
    :maxdepth: 1
    :hidden:

    quickstart
    install


.. toctree::
    :caption: OntologizerLib Usage
    :name: ontologizerlib-usage
    :maxdepth: 1
    :hidden:

    loading_ontologies
    iterating_ontologies


.. toctree::
    :caption: Project Info
    :name: project-info
    :maxdepth: 1
    :hidden:

    contributing
    authors
    history
    license

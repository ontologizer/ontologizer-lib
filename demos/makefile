#!/bin/make
#
# Simple makefile to build all demos in this folder
#
# Requires installed ontologizerlib artifacts.

DEMOS=$(sort $(wildcard */))

DEMOS_CLEAN_TARGETS=$(addsuffix clean,$(DEMOS))

.PHONY: all
all: $(DEMOS)


.PHONY: $(DEMOS)
$(DEMOS):
	cd $@ && mvn package

,PHONY: $(DEMOS_CLEAN_TARGETS)
$(DEMOS_CLEAN_TARGETS):
	cd $(dir $@) && mvn $(notdir $@)

.PHONY: clean
clean: $(DEMOS_CLEAN_TARGETS)

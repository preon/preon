========
 README
========

Preon aims to provide a framework for dealing with binary encoded
data; or more specifically, it aims to deal with situations in which
bytes are considered to be overkill. The project is named after the
"point-like" particles, conceived to be subcomponents of quarks and
leptons. Let's just say very small particles that you don't see with
the naked eye, but you probably rely on them without knowing.

Preon is to bitstream encoded content what JAXB is to XML, or
Hibernate to relational databases. You define your in memory
representation of the data structure in Java classes, and add
annotations to 'tell' how it should be mapped onto a bitstream encoded
representation. Preon takes care of the rest: it will give you a
decoder, hyperlinked documentation on the encoding format, and - if
you want - annotated hexdumps explaining you *exactly* what you're
looking at.

Distinctive features
====================

* Preon does *not* a assume a finite set of compression
  algorithms. There are many ways to compress data. Preon allows
  compression experts to add components that from that part on become
  part of Preon's declarative language. Preon is extensible.
* Preon is capable of generating documentation that would not look
  bad on Wikipedia. 
* Preon does *not* assume all data can be loaded in memory in a single
  go. Instead, it will pull data in on demand, only if it's
  needed. All logic required to understand how to jump to different
  parts of the file are hidden from the user.
* Preon does *not* assume there will only be a single thread
  consuming the data.
* Preon is declarative in nature, but the dependencies between
  different data elements inside an encoded representation can be
  modelled as complex expressions.
* Preon was used to creata a Java bytecode parser without writing a
  single line of imperative code.

More documentation
==================

* `Bit Syntax for Java <http://dl.acm.org/citation.cfm?id=1639955>`_
  Wilfred Springer, 2009, Proceedings of the 24th ACM SIGPLAN
  conference companion on Object oriented programming systems
  languages and applications
* `OOPSLA slides
  <http://www.slideshare.net/springerw/oopsla-talk-on-preon>`_
* `Preon Introduction
  <http://www.scribd.com/doc/8128172/Preon-Introduction>`_
* `Preon Under the Hood
  <http://www.scribd.com/doc/7988375/Preon-Under-the-Hood>`_

Issue tracker, JavaDocs, etc.
=============================

We gratefully use the issue tracker and other services provided by
Codehaus. The Maven generated documentation can be found `over there
<http://preon.codehaus.org/>`_, as well as the issue tracker, JavaDoc
documentation and a bunch of other things.



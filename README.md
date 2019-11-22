# Jolie

This is the source code repository of the [Jolie programming language](https://www.jolie-lang.org).

See [this page](https://jolie-lang.org/downloads.html) for installation instructions.

[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/jolie/jolie.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/jolie/jolie/context:java) ![Travis-CI](https://travis-ci.org/jolie/jolie.svg?branch=master)

## Jolie Module support

This branch consist of development of Module system support for Jolie 2.0.

## Task List

- [ ] Adding `import` keyword. See syntax below.
- [ ] modify `service` keyword to be main execution block of a jolie application

### Import syntax

```BNF
import_stmt ::= "import" import_clause from_clause
import_clause ::= namespace_import | import_list
namespace_import ::= * "as" import_binding
import_list ::= import_specifier | import_list , import_specifier
import_specifier ::= import_binding | identifier_name as import_binding
import_binding ::= binding_identifier
from_clause ::= "from" module_specifier
module_specifier ::= string_literal
```

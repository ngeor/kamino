# checkstyle-rules

Rules for [checkstyle]. Share rules among projects.

[![Build Status](https://travis-ci.org/ngeor/checkstyle-rules.svg?branch=master)](https://travis-ci.org/ngeor/checkstyle-rules)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.ngeor/checkstyle-rules.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.ngeor%22%20AND%20a:%22checkstyle-rules%22)

## Overview

The rules are based on the default Sun checks, with some modifications.

## Usage

You can configure the maven-checkstyle-plugin in this way:

```xml
<build>
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.1.0</version>
    <executions>
      <execution>
        <id>validate</id>
        <phase>validate</phase>
        <goals>
          <goal>check</goal>
        </goals>
      </execution>
    </executions>
    <dependencies>
      <dependency>
        <groupId>com.puppycrawl.tools</groupId>
        <artifactId>checkstyle</artifactId>
        <version>8.29</version>
      </dependency>
      <dependency>
        <groupId>com.github.ngeor</groupId>
        <artifactId>checkstyle-rules</artifactId>
        <version>4.0.1</version>
      </dependency>
    </dependencies>
    <configuration>
      <configLocation>com/github/ngeor/checkstyle.xml</configLocation>
      <includeTestSourceDirectory>true</includeTestSourceDirectory>
    </configuration>
  </plugin>
</build>
```

## Rules in details

### [Annotations](https://checkstyle.sourceforge.io/config_annotation.html)

| Rule                   | Status   |
| ---------------------- | -------- |
| AnnotationLocation     | Not Used |
| AnnotationOnSameLine   | Not Used |
| AnnotationUseStyle     | Not Used |
| MissingDeprecated      | Not Used |
| MissingOverride        | Not Used |
| PackageAnnotation      | Not Used |
| SuppressWarnings       | Not Used |
| SuppressWarningsHolder | Used     |

### [Block Checks](https://checkstyle.sourceforge.io/config_blocks.html)

| Rule              | Status   |
| ----------------- | -------- |
| AvoidNestedBlocks | Used     |
| EmptyBlock        | Used     |
| EmptyCatchBlock   | Not Used |
| LeftCurly         | Used     |
| NeedBraces        | Used     |
| RightCurly        | Used     |

### [Class Design](https://checkstyle.sourceforge.io/config_design.html)

| Rule                        | Status     |
| --------------------------- | ---------- |
| DesignForExtension          | _Disabled_ |
| FinalClass                  | Used       |
| HideUtilityClassConstructor | Used       |
| InnerTypeLast               | Not Used   |
| InterfaceIsType             | Used       |
| MutableException            | Not Used   |
| OneTopLevelClass            | _Enabled_  |
| ThrowsCount                 | Not Used   |
| VisibilityModifier          | Used       |

### [Coding](https://checkstyle.sourceforge.io/config_coding.html)

| Rule                                           | Status     |
| ---------------------------------------------- | ---------- |
| ArrayTrailingComma                             | Not Used   |
| AvoidInlineConditionals                        | _Disabled_ |
| CovariantEquals                                | _Enabled_  |
| DeclarationOrder                               | _Enabled_  |
| DefaultComesLast                               | _Enabled_  |
| EmptyStatement                                 | Used       |
| EqualsAvoidNull                                | _Enabled_  |
| EqualsHashCode                                 | Used       |
| ExplicitInitialization                         | _Enabled_  |
| FallThrough                                    | _Enabled_  |
| FinalLocalVariable                             | Not Used   |
| HiddenField                                    | _Altered_  |
| IllegalCatch                                   | _Enabled_  |
| IllegalInstantiation                           | Used       |
| IllegalThrows                                  | _Enabled_  |
| IllegalToken                                   | Not Used   |
| IllegalTokenText                               | Not Used   |
| IllegalType                                    | _Enabled_  |
| InnerAssignment                                | Used       |
| MagicNumber                                    | _Altered_  |
| MissingCtor                                    | Not Used   |
| MissingSwitchDefault                           | Used       |
| ModifiedControlVariable                        | _Enabled_  |
| MultipleStringLiterals                         | Not Used   |
| MultipleVariableDeclarations                   | _Enabled_  |
| NestedForDepth                                 | _Enabled_  |
| NestedIfDepth                                  | _Enabled_  |
| NestedTryDepth                                 | _Enabled_  |
| NoClone                                        | _Enabled_  |
| NoFinalizer                                    | _Enabled_  |
| OneStatementPerLine                            | _Enabled_  |
| OverloadMethodsDeclarationOrder                | _Enabled_  |
| PackageDeclaration                             | _Enabled_  |
| ParameterAssignment                            | _Enabled_  |
| RequireThis                                    | Not Used   |
| ReturnCount                                    | Not Used   |
| SimplifyBooleanExpression                      | Used       |
| SimplifyBooleanReturn                          | Used       |
| StringLiteralEquality                          | _Enabled_  |
| SuperClone                                     | Not Used   |
| SuperFinalize                                  | Not Used   |
| UnnecessaryParentheses                         | _Enabled_  |
| UnnecessarySemicolonAfterTypeMemberDeclaration | Not Used   |
| UnnecessarySemicolonInEnumeration              | Not Used   |
| UnnecessarySemicolonInTryWithResources         | Not Used   |
| VariableDeclarationUsageDistance               | Not Used   |

#### HiddenField

- ignoreConstructorParameter set to true
- ignoreSetter set to true

#### MagicNumber

Allows magic numbers in hash code, field declarations and annotations.

### [Headers](https://checkstyle.sourceforge.io/config_header.html)

| Rule         | Status     |
| ------------ | ---------- |
| Header       | _Disabled_ |
| RegexpHeader | Not Used   |

### [Imports](https://checkstyle.sourceforge.io/config_imports.html)

| Rule              | Status    |
| ----------------- | --------- |
| AvoidStarImport   | _Altered_ |
| AvoidStaticImport | Not Used  |
| CustomImportOrder | Not Used  |
| IllegalImport     | Used      |
| ImportControl     | Not Used  |
| ImportOrder       | _Altered_ |
| RedundantImport   | Used      |
| UnusedImports     | Used      |

#### AvoidStarImport

Allowing packages:

- `java.awt`
- `java.io`
- `java.util`
- `javax.swing`
- `java.lang.Math`
- `org.junit.Assert`
- `org.mockito.Mockito`
- `org.springframework.test.web.servlet.result.MockMvcResultMatchers`

#### ImportOrder

- All non-static imports in one group, alphabetically sorted
- One line separator
- All static imports in one group, alphabetically sorted

### [Javadoc Comments](https://checkstyle.sourceforge.io/config_javadoc.html)

| Rule                              | Status                        |
| --------------------------------- | ----------------------------- |
| AtclauseOrder                     | Not Used                      |
| InvalidJavadocPosition            | Not Used                      |
| JavadocBlockTagLocation           | Not Used                      |
| JavadocMethod                     | _Altered_                     |
| JavadocPackage                    | _Disabled_                    |
| JavadocParagraph                  | Not Used                      |
| JavadocStyle                      | Used                          |
| JavadocTagContinuationIndentation | Not Used                      |
| JavadocType                       | Used                          |
| JavadocVariable                   | _Altered_                     |
| MissingJavadocMethod              | _Altered_ (see JavadocMethod) |
| MissingJavadocPackage             | Not Used                      |
| MissingJavadocType                | _Altered_ (see JavadocType)   |
| NonEmptyAtclauseDescription       | Not Used                      |
| SingleLineJavadoc                 | Not Used                      |
| SummaryJavadoc                    | Not Used                      |
| WriteTag                          | Not Used                      |

#### JavadocMethod

- No need to document private methods
- Allow missing params, throws, return tags
- No need to document methods less than 2 lines long
- No need to document getters and setters
- No need to document methods annotated with one of Override, Test,
  ParameterizedTest, Before, BeforeClass, BeforeEach, BeforeAll, After,
  AfterClass, AfterEach, AfterAll, DisplayName

#### JavadocVariable

- No need to document private fields

### [Metrics](https://checkstyle.sourceforge.io/config_metrics.html)

| Rule                         | Status   |
| ---------------------------- | -------- |
| BooleanExpressionComplexity  | Not Used |
| ClassDataAbstractionCoupling | Not Used |
| ClassFanOutComplexity        | Not Used |
| CyclomaticComplexity         | Not Used |
| JavaNCSS                     | Not Used |
| NPathComplexity              | Not Used |

### [Miscellaneous](https://checkstyle.sourceforge.io/config_misc.html)

| Rule                          | Status     |
| ----------------------------- | ---------- |
| ArrayTypeStyle                | Used       |
| AvoidEscapedUnicodeCharacters | Not Used   |
| CommentsIndentation           | _Enabled_  |
| DescendantToken               | Not Used   |
| FinalParameters               | _Disabled_ |
| Indentation                   | _Enabled_  |
| NewlineAtEndOfFile            | Used       |
| OrderedProperties             | Not Used   |
| OuterTypeFilename             | _Enabled_  |
| TodoComment                   | _Disabled_ |
| TrailingComment               | Not Used   |
| Translation                   | Used       |
| UncommentedMain               | Not Used   |
| UniqueProperties              | Not Used   |
| UpperEll                      | Used       |

### [Modifiers](https://checkstyle.sourceforge.io/config_modifier.html)

| Rule                           | Status   |
| ------------------------------ | -------- |
| ClassMemberImpliedModifier     | Not Used |
| InterfaceMemberImpliedModifier | Not Used |
| ModifierOrder                  | Used     |
| RedundantModifier              | Used     |

### [Naming Conventions](https://checkstyle.sourceforge.io/config_naming.html)

| Rule                       | Status    |
| -------------------------- | --------- |
| AbbreviationAsWordInName   | Not Used  |
| AbstractClassName          | Not Used  |
| CatchParameterName         | Not Used  |
| ClassTypeParameterName     | Not Used  |
| ConstantName               | Used      |
| InterfaceTypeParameterName | Not Used  |
| LambdaParameterName        | Not Used  |
| LocalFinalVariableName     | Used      |
| LocalVariableName          | Used      |
| MemberName                 | Used      |
| MethodName                 | _Altered_ |
| MethodTypeParameterName    | Not Used  |
| PackageName                | _Altered_ |
| ParameterName              | Used      |
| StaticVariableName         | Used      |
| TypeName                   | Used      |

#### MethodName

Using Google flavor to permit underscore in test method names:
`^[a-z][a-z0-9][a-zA-Z0-9_]*$"`

#### PackageName

Limited to lowercase letters and numbers: `^[a-z]+(\.[a-z][a-z0-9]*)*$`

### [Regexp](https://checkstyle.sourceforge.io/config_regexp.html)

| Rule                 | Status    |
| -------------------- | --------- |
| Regexp               | Not Used  |
| RegexpMultiline      | Not Used  |
| RegexpOnFilename     | Not Used  |
| RegexpSingleline     | _Altered_ |
| RegexpSinglelineJava | Not Used  |

#### RegexSingleline

Verifies that lines do not have trailing spaces.

### [Size Violations](https://checkstyle.sourceforge.io/config_sizes.html)

| Rule                     | Status                                |
| ------------------------ | ------------------------------------- |
| AnonInnerLength          | Not Used                              |
| ExecutableStatementCount | Not Used                              |
| FileLength               | Used                                  |
| LineLength               | _Altered_ increased to 120 characters |
| MethodCount              | Not Used                              |
| MethodLength             | Used                                  |
| OuterTypeNumber          | Not Used                              |
| ParameterNumber          | Used                                  |

### [Whitespace](https://checkstyle.sourceforge.io/config_whitespace.html)

| Rule                   | Status    |
| ---------------------- | --------- |
| EmptyForInitializerPad | Not Used  |
| EmptyForIteratorPad    | Used      |
| EmptyLineSeparator     | _Altered_ |
| FileTabCharacter       | Used      |
| GenericWhitespace      | Used      |
| MethodParamPad         | Used      |
| NoLineWrap             | Not Used  |
| NoWhitespaceAfter      | Used      |
| NoWhitespaceBefore     | Used      |
| OperatorWrap           | Used      |
| ParenPad               | Used      |
| SeparatorWrap          | Not Used  |
| SingleSpaceSeparator   | Not Used  |
| TypecastParenPad       | Used      |
| WhitespaceAfter        | Used      |
| WhitespaceAround       | _Altered_ |

#### EmptyLineSeparator

- allowNoEmptyLineBetweenFields set to true
- allowMultipleEmptyLines set to false

#### WhitespaceAround

- allows empty constructor bodies
- allows empty methods
- allows empty class, interface and enum bodies

## Suppressions

It is possible to specify an
[XML file with suppressions](http://checkstyle.sourceforge.net/config_filters.html#SuppressionFilter).
The file is optional and it needs to be in `checkstyle/suppressions.xml`.

Example file to ignore magic numbers and multiple string literals in unit tests:

```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suppressions PUBLIC
    "-//Puppy Crawl//DTD Suppressions 1.0//EN"
    "http://checkstyle.sourceforge.net/dtds/suppressions_1_0.dtd">
<suppressions>
    <suppress files="Test.java$" checks="MagicNumber" />
    <suppress files="Test.java$" checks="MultipleStringLiterals" />
</suppressions>
```

It is also possible to suppress violations using the `@SuppressWarnings`
annotation.

[checkstyle]: https://checkstyle.sourceforge.io/

## Java Expert

A simple implementation of a Java expert system using the Java programming language. The system is designed to demonstrate the basic principles of expert systems, including knowledge representation, inference, and rule-based reasoning.

Note: This was implemented for a brazilian university course, so the system is in portuguese. 

### Describing a Expert

To describe an expert, we need to define attributes and rules. We do this using a language called "Linguagem de Descrição de Especialista" (LDE). The LDE is a simple language that allows us to define the characteristics and behavior of an expert in a structured way. 

### Attributes 

To define an expert, we need to define its attributes. Attributes are the characteristics that describe the things that an expert may know. For example, to decide if a patient has a disease, we need to know the symptoms of the disease.

```
// `ATRIBUTO` is the keyword to define an attribute
// `TEXTO` is the type of the attribute
// `symptons` is the name of the attribute
// `headache`, `fever`, and `nausea` are the possible values of the attribute
ATRIBUTO "symptons" TEXTO (
    "headache",
    "fever",
    "nausea"
)
```

When evaluating the rules, the expert must know beforehand what is the attribute that is the object of the evaluation. In this case, we can have something like `has_disease`.

```
ATRIBUTO "has_disease" TEXTO (
    "yes",
    "no"
)
```

Then, we need to explicit say what is the attribute that is the object of the evaluation:

```
OBJETIVOS (
    "has_disease"
)
```

### Rules

Then, we need to define the rules. Rules are the conditions that determine the behavior of the expert. For example, if a patient has a headache and a fever, then the patient has a disease.

```
// `REGRA` is the keyword to define a rule
// `headache and fever` is the name of the rule
// `SE` is the keyword to define the condition of the rule
// `OU` is the keyword to define an OR condition
// `ENTAO` is the keyword to define the action of the rule

REGRA "headache and fever" (
  SE 
    "symptons" = "headache" 
  OU "symptons" = "fever"
  ENTAO
    "has_disease" = "yes"
)
```

### UI

After defining the attributes and rules, we need to create a user interface (UI) to interact with the expert system. The UI allows the user to input data and receive feedback from the expert system.

We have to create an `QuizManager` in the main class, and add the questions to the quiz. The `QuizManager` is responsible for managing the quiz and displaying the questions to the user. 

```java
    private static void presenteExample()  {
        var manager = new QuizManager("Exemplo: Presente", Expert.fromFile("example_numerico.ex"));
        manager.addNumericQuestion("Qual a idade do aniversariante?", "idade");
        manager.addOptionsQuestion("Qual é o sexo do aniversáriante?", "sexo");
        manager.runQuiz();
    }
```

### Examples

We have a lot of examples in the main class, and the corresponding `.ex` (./src/main/resources) files are the description of the experts. 
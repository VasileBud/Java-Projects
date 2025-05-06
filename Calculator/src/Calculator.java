import javax.swing.*;
import java.awt.*;
import java.util.Stack;

public class Calculator extends JFrame {
    private JPanel panelCalculator;
    private JPanel panelButton;
    private JPanel panelDisplay;
    private JButton delButton;
    private JButton nineButton;
    private JButton devideButton;
    private JButton multiplayButton;
    private JButton percentButton;
    private JButton eightButton;
    private JButton clearButton;
    private JButton sevenButton;
    private JButton fourButton;
    private JButton fiveButton;
    private JButton sixButton;
    private JButton subButton;
    private JButton oneButton;
    private JButton twoButton;
    private JButton threeButton;
    private JButton addButton;
    private JTextField display;
    private JButton reverseButton;
    private JButton zeroButton;
    private JButton commaButton;
    private JButton equalButton;

    private StringBuilder input = new StringBuilder();

    public Calculator() {
        this.setTitle("Calculator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(420, 550);
        this.setLocationRelativeTo(null);
        this.setContentPane(this.panelCalculator);

        display.setEditable(false);

        addNumberAction(zeroButton, "0");
        addNumberAction(oneButton, "1");
        addNumberAction(twoButton, "2");
        addNumberAction(threeButton, "3");
        addNumberAction(fourButton, "4");
        addNumberAction(fiveButton, "5");
        addNumberAction(sixButton, "6");
        addNumberAction(sevenButton, "7");
        addNumberAction(eightButton, "8");
        addNumberAction(nineButton, "9");

        addOperatorAction(addButton, "+");
        addOperatorAction(subButton, "-");
        addOperatorAction(multiplayButton, "*");
        addOperatorAction(devideButton, "/");

        this.equalButton.addActionListener(e -> calculateResult());

        this.percentButton.addActionListener(e -> {
            if (input.length() > 0 && !endsWithOperator()) {
                try {
                    double value = Double.parseDouble(input.toString()) / 100;
                    input.setLength(0);
                    input.append(value);
                    display.setText(input.toString());
                } catch (Exception ex) {
                    display.setText("Eroare");
                    input.setLength(0);
                }
            }
        });

        this.commaButton.addActionListener(e -> {
            if (input.length() > 0 && !input.toString().endsWith(".")) {
                input.append(".");
                display.setText(input.toString());
            }
        });

        this.delButton.addActionListener(e -> {
            if (input.length() > 0) {
                input.deleteCharAt(input.length() - 1);
                display.setText(input.toString());
            }
        });

        this.clearButton.addActionListener(e -> {
            input.setLength(0);
            display.setText("");
        });

        this.reverseButton.addActionListener(e -> {
            if (input.length() > 0) {
                if (input.charAt(0) == '-') {
                    input.deleteCharAt(0);
                } else {
                    input.insert(0, "-");
                }
                display.setText(input.toString());
            }
        });

        this.setVisible(true);
    }

    private void addNumberAction(JButton button, String number) {
        button.addActionListener(e -> {
            input.append(number);
            display.setText(input.toString());
        });
    }

    private void addOperatorAction(JButton button, String operator) {
        button.addActionListener(e -> {
            if (input.length() > 0 && !endsWithOperator()) {
                input.append(" ").append(operator).append(" ");
                display.setText(input.toString());
            }
        });
    }

    private boolean endsWithOperator() {
        return input.toString().matches(".*[+\\-*/] $");
    }

    private void calculateResult() {
        try {
            String result = evaluateExpression(input.toString());
            display.setText(result);
            input.setLength(0);
            input.append(result);
        } catch (Exception e) {
            display.setText("Eroare");
            input.setLength(0);
        }
    }

    private String evaluateExpression(String expression) {
        try {
            String[] tokens = expression.split(" ");
            Stack<Double> numbers = new Stack<>();
            Stack<String> operators = new Stack<>();

            for (String token : tokens) {
                if (token.matches("-?\\d+(\\.\\d+)?")) { // Număr
                    numbers.push(Double.parseDouble(token));
                } else if (token.matches("[+\\-*/]")) { // Operator
                    while (!operators.isEmpty() && precedence(token) <= precedence(operators.peek())) {
                        double b = numbers.pop();
                        double a = numbers.pop();
                        String op = operators.pop();
                        numbers.push(applyOperator(a, b, op));
                    }
                    operators.push(token);
                }
            }

            while (!operators.isEmpty()) {
                double b = numbers.pop();
                double a = numbers.pop();
                String op = operators.pop();
                numbers.push(applyOperator(a, b, op));
            }

            return String.valueOf(numbers.pop());
        } catch (Exception e) {
            return "Eroare";
        }
    }

    private int precedence(String operator) {
        switch (operator) {
            case "*":
            case "/":
                return 2;
            case "+":
            case "-":
                return 1;
            default:
                return 0;
        }
    }

    private double applyOperator(double a, double b, String operator) {
        switch (operator) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": return (b != 0) ? a / b : Double.NaN;
            default: return 0;
        }
    }

    public static void main(String[] args) {
        new Calculator();
    }
}

import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.Computable;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.FieldValue;
import org.dmg.pmml.PMML;
import org.jpmml.model.PMMLUtil;
import org.jpmml.evaluator.FieldName;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PMMLPredictor {
    public static void main(String[] args) {
        try {
            // Load the PMML model
            File pmmlFile = new File("bestmodel/xgboost_model.pmml");
            FileInputStream inputStream = new FileInputStream(pmmlFile);
            PMML pmml = PMMLUtil.unmarshal(inputStream);
            ModelEvaluator<?> evaluator = (ModelEvaluator<?>) ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml);
            evaluator.verify();

            // Define input data
            Map<FieldName, Object> inputData = new LinkedHashMap<>();
            inputData.put(new FieldName("sex"), 0);
            inputData.put(new FieldName("age_group"), 3);
            inputData.put(new FieldName("Initiation_Year"), 1);
            inputData.put(new FieldName("entrypoint"), 5);
            inputData.put(new FieldName("regime_change"), 1);
            inputData.put(new FieldName("pregnancy"), 0);
            inputData.put(new FieldName("TB"), 1);
            inputData.put(new FieldName("facility_level"), 3);
            inputData.put(new FieldName("Contact"), 0);
            inputData.put(new FieldName("season"), 1);
            inputData.put(new FieldName("who_stage"), 1);
            inputData.put(new FieldName("treatment_duration"), 4);
            inputData.put(new FieldName("advanced_hiv"), 0);
            inputData.put(new FieldName("no_IIT_ever"), 1);
            inputData.put(new FieldName("no_IIT_in_last12months"), 0);
            inputData.put(new FieldName("visits_current_regimen"), 0);
            inputData.put(new FieldName("number_visits"), 0);

            // Set up input fields
            Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();
            List<InputField> inputFields = evaluator.getInputFields();
            for (InputField inputField : inputFields) {
                FieldName inputName = inputField.getName();
                Object rawValue = inputData.get(inputName);
                FieldValue inputValue = inputField.prepare(rawValue);
                arguments.put(inputName, inputValue);
            }

            // Perform prediction
            Map<FieldName, ?> results = evaluator.evaluate(arguments);
            FieldName targetFieldName = evaluator.getTargetField().getName();
            Object prediction = results.get(targetFieldName);

            if (prediction instanceof Computable) {
                prediction = ((Computable) prediction).getResult();
            }

            System.out.println("Prediction: " + prediction);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

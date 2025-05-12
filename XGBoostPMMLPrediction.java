import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.OutputField;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.Computable;
import org.jpmml.model.PMMLUtil;
import org.dmg.pmml.PMML;
import org.dmg.pmml.FieldName;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import org.xml.sax.SAXException;

public class XGBoostPMMLPrediction {

    public static void main(String[] args) {
        try {
            // Load PMML model
            InputStream pmmlStream = new FileInputStream("bestmodel/xgboost_model.pmml");
            PMML pmml = PMMLUtil.unmarshal(pmmlStream);

            // Create Model Evaluator
            ModelEvaluator<?> modelEvaluator = (ModelEvaluator<?>) ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml);
            modelEvaluator.verify();

            // Prepare input data
            Map<String, Object> inputData = new LinkedHashMap<>();
            inputData.put("sex", 0);
            inputData.put("age_group", 3);
            inputData.put("Initiation_Year", 1);
            inputData.put("entrypoint", 5);
            inputData.put("regime_change", 1);
            inputData.put("pregnancy", 0);
            inputData.put("TB", 1);
            inputData.put("facility_level", 3);
            inputData.put("Contact", 0);
            inputData.put("season", 1);
            inputData.put("who_stage", 1);
            inputData.put("treatment_duration", 4);
            inputData.put("advanced_hiv", 0);
            inputData.put("no_IIT_ever", 1);
            inputData.put("no_IIT_in_last12months", 0);
            inputData.put("visits_current_regimen", 0);
            inputData.put("number_visits", 0);

            // Convert input data to FieldValues
            Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();
            List<InputField> inputFields = modelEvaluator.getInputFields();
            for (InputField inputField : inputFields) {
                Object rawValue = inputData.get(inputField.getName().getValue());
                FieldValue inputValue = inputField.prepare(rawValue);
                arguments.put(inputField.getName(), inputValue);
            }

            // Evaluate model
            Map<FieldName, ?> results = modelEvaluator.evaluate(arguments);

            // Extract result
            for (OutputField outputField : modelEvaluator.getOutputFields()) {
                FieldName fieldName = outputField.getName();
                Object resultValue = results.get(fieldName);

                if (resultValue instanceof Computable) {
                    resultValue = ((Computable) resultValue).getResult();
                }

                System.out.println("Prediction: " + resultValue);
            }
        } catch (IOException | SAXException | JAXBException e) {
            e.printStackTrace();
        }
    }
}


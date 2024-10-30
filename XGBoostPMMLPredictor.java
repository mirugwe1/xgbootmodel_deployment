import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.OutputField;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.TargetField;
import org.jpmml.evaluator.visitors.DefaultVisitorBattery;
import org.dmg.pmml.PMML;
import org.jpmml.model.PMMLUtil;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XGBoostPMMLPredictor {

    private ModelEvaluator<?> model;

    public XGBoostPMMLPredictor(String pmmlFilePath) throws Exception {
        this.model = loadModel(pmmlFilePath);
    }

    private ModelEvaluator<?> loadModel(String pmmlFilePath) throws Exception {
        try (FileInputStream inputStream = new FileInputStream(new File(pmmlFilePath))) {
            PMML pmml = PMMLUtil.unmarshal(new StreamSource(inputStream));
            ModelEvaluatorFactory factory = ModelEvaluatorFactory.newInstance();
            return factory.newModelEvaluator(pmml);
        }
    }

    public Object predict(Map<String, Object> inputData) {
        Map<String, FieldValue> arguments = new HashMap<>();

        // Prepare input fields
        for (InputField inputField : model.getInputFields()) {
            String fieldName = inputField.getName().getValue();
            Object rawValue = inputData.get(fieldName);
            FieldValue fieldValue = inputField.prepare(rawValue);
            arguments.put(inputField.getName().getValue(), fieldValue);
        }

        // Perform prediction
        Map<String, ?> results = model.evaluate(arguments);

        // Extract the result
        List<TargetField> targetFields = model.getTargetFields();
        Object prediction = results.get(targetFields.get(0).getName().getValue());

        return prediction;
    }

    public static void main(String[] args) throws Exception {
        String pmmlFilePath = "bestmodel/xgboost_model.pmml";  // Path to your PMML model

        // Create a new predictor instance
        XGBoostPMMLPredictor predictor = new XGBoostPMMLPredictor(pmmlFilePath);

        // Prepare input data
        Map<String, Object> inputData = new HashMap<>();
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

        // Perform prediction
        Object prediction = predictor.predict(inputData);

        // Print the prediction result
        System.out.println("Prediction: " + prediction);
    }
}

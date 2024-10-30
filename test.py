from pypmml import Model

model = Model.load('bestmodel/xgboost_model.pmml')

input_data = {
    'sex': 0,
    'age_group': 3,
    'Initiation_Year': 1,
    'entrypoint': 5,
    'regime_change': 1,
    'pregnancy': 0,
    'TB': 1,
    'facility_level': 3,
    'Contact': 0,
    'season': 1,
    'who_stage': 1,
    'treatment_duration': 4,
    'advanced_hiv': 0,
    'no_IIT_ever': 1,
    'no_IIT_in_last12months': 0,
    'visits_current_regimen': 0,
    'number_visits': 0,
}

prediction = model.predict(input_data)
print('Prediction:', prediction)

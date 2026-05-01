from ml.signbridge_model import LANDMARK_DIM, WINDOW_FRAMES, build_model_config, class_count


def test_model_contract_shapes_match_android_tensor():
    config = build_model_config()

    assert WINDOW_FRAMES == 30
    assert LANDMARK_DIM == 1629
    assert config["input_shape"] == [None, 30, 1629]
    assert config["output_shape"] == [None, 26]
    assert class_count() == 26

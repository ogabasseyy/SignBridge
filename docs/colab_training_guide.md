# Training SignBridge on Google Colab

Because the ASL dataset is 46GB and training a Transformer from scratch is computationally heavy, you should train the model on Google Colab. Colab gives you access to a free NVIDIA T4 GPU and blazing-fast internet (downloading 46GB takes minutes on Google's servers, compared to hours on your Mac).

Here is the step-by-step guide to run the training pipeline we just built:

## Step 1: Set Up the Colab Environment
1. Go to [colab.research.google.com](https://colab.research.google.com) and click **New Notebook**.
2. Go to **Runtime > Change runtime type**. Select **T4 GPU** and save.
3. In the first cell, mount your Google Drive so you can save the final model permanently:
   ```python
   from google.colab import drive
   drive.mount('/content/drive')
   ```

## Step 2: Upload Your Code
Run this cell to clone your project (or you can manually drag-and-drop the `ml/` folder into the Colab file explorer on the left):
```bash
!git clone https://github.com/YOUR_GITHUB_USERNAME/Signbridge.git
%cd Signbridge
!pip install -r requirements.txt # (or pip install tensorflow datasets mediapipe opencv-python-headless)
```

## Step 3: Download the Datasets (Directly to Colab)
Instead of downloading the data to your Mac, Colab will download it directly.
First, set up your Kaggle API key (you can get this from your Kaggle Account settings -> "Create New API Token"):
```bash
import os
os.environ['KAGGLE_USERNAME'] = "your_kaggle_username"
os.environ['KAGGLE_KEY'] = "your_kaggle_api_key"

!kaggle datasets download abd0kamel/asl-citizen -p /content/data/asl --unzip
```

## Step 4: Extract the Landmarks
Run the extraction scripts we built. Because Colab has powerful CPUs, this will chew through the images very quickly.
```bash
# 1. Extract ASL
!python ml/scripts/extract_landmarks_from_images.py --dataset /content/data/asl --output ml/private/asl_citizen/asl_landmarks.jsonl

# 2. Extract NSL (Lanfrica) - Requires Hugging Face Token!
!HF_TOKEN="your_hf_token" python ml/scripts/extract_landmarks_from_images.py --dataset Lanfrica/sign-to-speech-for-sign-language-understanding-a-case-study-of-nigerian-sign-language --output ml/private/lanfrica/nsl_landmarks.jsonl
```

## Step 5: Run the Training Pipeline
Run the bash script we created that automatically handles the ASL pre-training and NSL fine-tuning:
```bash
!chmod +x ml/scripts/pretrain_on_asl.sh
!./ml/scripts/pretrain_on_asl.sh
```

## Step 6: Save the Results
Once the script finishes, copy the final lightweight `.tflite` model into your Google Drive so you can download it to your Mac and put it in your Android app!
```bash
!cp app/src/main/assets/signbridge_model.tflite /content/drive/MyDrive/signbridge_model.tflite
```

After doing this, you just download that 1.27MB file from your Google Drive, place it in the `app/src/main/assets/` folder of your Mac project, and your app is officially fully trained and ready for the hackathon!

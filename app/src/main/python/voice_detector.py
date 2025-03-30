import os
import torch
import numpy as np
import json
from torch import nn

class VoiceDetector:
    def __init__(self, model_path, config_path, device='cpu'):
        self.device = device
        self.model_path = model_path
        self.sample_rate = 16000
        
        # Load model configuration
        with open(config_path, "r") as f_json:
            config = json.loads(f_json.read())
        
        # Initialize model
        self.model = self._load_model(config["model_config"])
        
        # Set model to evaluation mode
        self.model.eval()
        
    def _load_model(self, model_config):
        # This is a placeholder - you'll need to implement the actual Model class
        # or import it from your SAMO package
        from aasist.AASIST import Model
        
        # Initialize the model
        model = Model(model_config).to(self.device)
        
        # Load pre-trained weights
        checkpoint = torch.load(self.model_path, map_location=self.device)
        
        # Handle both DataParallel and regular model
        if isinstance(checkpoint, nn.DataParallel):
            model.load_state_dict(checkpoint.module.state_dict())
        else:
            model.load_state_dict(checkpoint.state_dict(), strict=False)
            
        return model
    
    def preprocess_audio(self, audio_data):
        """Preprocess audio data for model input"""
        # Convert Java array to NumPy array if needed
        if not isinstance(audio_data, np.ndarray):
            audio_data = np.array(audio_data)
        
        # Convert to mono if needed
        if len(audio_data.shape) > 1:
            audio_data = audio_data[:, 0]
        
        # Ensure correct sample rate and convert to float32
        audio = audio_data.astype(np.float32)
        
        # Normalize audio
        audio = audio / (np.max(np.abs(audio)) + 1e-8)
        
        # Convert to tensor and add batch dimension
        audio_tensor = torch.FloatTensor(audio).to(self.device)
        audio_tensor = audio_tensor.unsqueeze(0)
        
        return audio_tensor

    def predict(self, audio_data):
        """Predict if audio is real or fake"""
        # Preprocess audio
        audio_tensor = self.preprocess_audio(audio_data)
        
        # Make prediction
        with torch.no_grad():
            feats, output = self.model(audio_tensor)
            
            # Convert to numpy and get the score
            score = output.cpu().numpy()
            
            # Take the mean or first element of the score array
            if score.size > 1:
                score_value = float(score.mean())
            else:
                score_value = float(score.item())
                
        return score_value

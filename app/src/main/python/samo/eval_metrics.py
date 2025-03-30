import numpy as np
from scipy import optimize

def compute_eer(bonafide_scores, spoof_scores):
    """
    Compute Equal Error Rate (EER) given bonafide and spoof scores.
    
    Args:
        bonafide_scores: Scores for bonafide (real) samples
        spoof_scores: Scores for spoof (fake) samples
        
    Returns:
        eer: Equal Error Rate
        threshold: Threshold at which FAR = FRR
    """
    # Compute FAR and FRR for different thresholds
    thresholds = np.sort(np.concatenate((bonafide_scores, spoof_scores)))
    
    # Calculate FAR and FRR for each threshold
    far = np.zeros(thresholds.shape)
    frr = np.zeros(thresholds.shape)
    
    for i, threshold in enumerate(thresholds):
        far[i] = np.sum(spoof_scores >= threshold) / len(spoof_scores)
        frr[i] = np.sum(bonafide_scores < threshold) / len(bonafide_scores)
    
    # Find the threshold where FAR = FRR
    idx = np.argmin(np.abs(far - frr))
    eer = (far[idx] + frr[idx]) / 2
    
    return eer, thresholds[idx]

def compute_tDCF(bonafide_scores, spoof_scores, p_target=0.95, c_miss=1, c_fa=1):
    """
    Compute tandem Detection Cost Function (t-DCF)
    
    Args:
        bonafide_scores: Scores for bonafide (real) samples
        spoof_scores: Scores for spoof (fake) samples
        p_target: Prior probability of target speaker
        c_miss: Cost of missing a target
        c_fa: Cost of false alarm
        
    Returns:
        min_tDCF: Minimum t-DCF
    """
    # Simplified t-DCF implementation
    # In practice, this would be more complex and consider ASV errors
    
    # Compute EER threshold
    _, threshold = compute_eer(bonafide_scores, spoof_scores)
    
    # Compute FRR and FAR at EER threshold
    frr = np.sum(bonafide_scores < threshold) / len(bonafide_scores)
    far = np.sum(spoof_scores >= threshold) / len(spoof_scores)
    
    # Simplified t-DCF calculation
    tDCF = p_target * c_miss * frr + (1 - p_target) * c_fa * far
    
    return tDCF

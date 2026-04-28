from flask import Flask, request, jsonify
import re
from ollama import chat  # import the correct function

app = Flask(__name__)

# Function for System Relevance of the disease

def symptom_relevance_score(text: str) -> float:
    text_lower = text.lower()
    score = sum(word in text_lower for word in health_keywords)
    return score / len(health_keywords)

def is_valid_symptom_text(text: str) -> bool:
    if not text or len(text.strip()) < 3:
        return False
    if re.fullmatch(r'([a-zA-Z])\1{2,}', text.strip()):
        return False
    if len(set(text.strip())) < 3:
        return False
    if not re.search(r'[a-zA-Z]', text):
        return False
    if len(text.split()) < 2:
        return False
    return symptom_relevance_score(text) >= 0.05

# API endpoint
@app.route("/analyze", methods=["POST"])
def analyze():
    try:
        data = request.get_json()
        symptoms = data.get("symptoms", "")

        if not symptoms:
            return jsonify({"error": "Missing 'symptoms' field"}), 400

        if not is_valid_symptom_text(symptoms):
            return jsonify({
                "analysis": "Please enter valid medical symptoms or describe what you are feeling more clearly."
            }), 400



        # Code for Reporting Prompt into it
        response = chat(
            model="tinyllama:latest",
            messages=[{"role": "user", "content": prompt}]
        )

        text = response.get("content", "No output.")

        return jsonify({"analysis": text.strip()})

    except Exception as e:
        return jsonify({"error": str(e)}), 500

# Run Flask app
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)

from flask import Flask, request, jsonify
from twilio.rest import Client
from twilio.twiml.voice_response import VoiceResponse

app = Flask(__name__)
#Twilio Setup
account_sid = "xxxxxxxxxxxxxxxxxxxxxxxx" #Your Twilio Account Sid
auth_token = "xxxxxxxxxxxxxxxxxxxxxxxxx"    #Your Auth Token
twilio_number = "+ xxxxxxxxxx"  # Your Twilio number

client = Client(account_sid, auth_token)



@app.route("/call", methods=["POST"])


# Function for making call

def make_call():
    data = request.get_json()
    print("Received data:", data)
    user_name = data.get("name", "User")
    mobile_number = data.get("number")
    latitude = data.get("latitude")
    longitude = data.get("longitude")
    

    location_message = ""
    if latitude and longitude:
        location_message = f"\n📍 Live Location: https://www.google.com/maps?q={latitude},{longitude}"
    else:
        location_message = "\n📍 Location not available."

    
    response = VoiceResponse()
    response.say(f"Hello {user_name}.  Emergency Alert!! This is a call from Kenoha. Please check upon your contact SMS immediately for more info.", 
                  voice="woman", language="en-GB")

   
    call = client.calls.create(
        to=mobile_number,
        from_=twilio_number,
        twiml=str(response),
        timeout=15
    )

    # sms = client.messages.create(
    #     body=f"🚨 Alert: {user_name}. An Emergency has been detected. Check immediately abouth the user! {location_message}",
    #     from_=twilio_number,
    #     to=mobile_number
    # )

    try:
        sms = client.messages.create(
            body=f"🚨 Alert from Kenoha! Emergency triggered by {user_name} . Check immediately about the user! {location_message}",
            from_=twilio_number,
            to=mobile_number
        )
        print("SMS SID:", sms.sid)
    except Exception as e:
        print("SMS error:", e)


    return jsonify({
        "status": "success",
        "call_sid": call.sid,
        "sms_sid": sms.sid,
        "message": "Call and SMS sent successfully."
    })

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)

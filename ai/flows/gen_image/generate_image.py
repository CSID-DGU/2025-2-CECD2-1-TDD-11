from promptflow.core import tool
import requests
import json
import os

@tool
def generate_image(autobiography_content: str, style: str = "natural", size: str = "1024x1024") -> str:
    """
    자서전 내용을 바탕으로 이미지 생성
    """
    try:
        # 하드코딩된 프롬프트 + 자서전 내용
        prompt = f'다음 내용은 자서전의 챕터이다. 해당 챕터의 표지를 작성하시오.: "{autobiography_content}"'
        
        api_key = None
        
        # 1. PromptFlow 연결에서 API 키 가져오기 시도
        try:
            from promptflow import PFClient
            pf = PFClient()
            connection = pf.connections.get("open_ai_connection")
            api_key = getattr(connection, 'api_key', None)
        except Exception:
            pass
        
        # 2. 환경변수에서 API 키 가져오기
        if not api_key:
            api_key = os.getenv("AZURE_OPENAI_API_KEY") or os.getenv("OPENAI_API_KEY")
        
        if not api_key:
            return "Error: No OpenAI API key found"
        
        # 유효한 크기로 변환
        valid_sizes = ["1024x1024", "1792x1024", "1024x1792"]
        if size not in valid_sizes:
            size = "1024x1024"
        
        # OpenAI API 직접 호출
        headers = {
            "Authorization": f"Bearer {api_key}",
            "Content-Type": "application/json"
        }
        
        data = {
            "model": "dall-e-3",
            "prompt": prompt,
            "style": style,
            "size": size,
            "quality": "standard",
            "n": 1
        }
        
        response = requests.post(
            "https://api.openai.com/v1/images/generations",
            headers=headers,
            json=data,
            timeout=60
        )
        
        if response.status_code == 200:
            result = response.json()
            if result.get("data") and len(result["data"]) > 0:
                return result["data"][0]["url"]
            else:
                return "Error: No image generated"
        else:
            return f"Error: {response.status_code} - {response.text}"
        
    except Exception as e:
        return f"Error: {str(e)}"
## Installation

1. copy build.gradle.sample to build.gradle
2. provide a value for spamBlockerApiUrl
3. The API URL must return a response of the following json:

### Request 

```
${spamBlockerApiUrl}=PHONE_NUMBER
```

### Response

```
{
  "score": Integer ranging from 0 - 1
}
```

1 indicates it's spam.




namespace EzBuildServicesMessaging.Base
{
    public class BaseRequest
    {

    }

    public class BaseResponse
    {
        public bool IsSuccessful { get; set; }
        public string Message { get; set; }

        public BaseResponse()
        {
            IsSuccessful = true;
        }
    }
}

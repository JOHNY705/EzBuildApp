//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace EzBuildDataAccess
{
    using System;
    using System.Collections.Generic;
    
    public partial class Meeting
    {
        public int IDMeeting { get; set; }
        public string Title { get; set; }
        public System.DateTime MeetingDate { get; set; }
        public System.TimeSpan MeetingStartTime { get; set; }
        public string MeetingDuration { get; set; }
        public string MeetingDescription { get; set; }
        public int EmployeeID { get; set; }
    
        public virtual Employee Employee { get; set; }
    }
}

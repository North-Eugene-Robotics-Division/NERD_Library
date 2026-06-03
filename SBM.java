/* 
Hello! Welcome to the State Based Machine! This program can be used for any year's robot, and functions as a tracking list of all 
running motors and servos. By passing a motor along with some other information, this program can track how long any given object 
has been running, and then stop/change its power/position when the target time is reached.
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import java.util.List;
import java.lang.reflect.Array;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import java.util.ArrayList;
import java.util.Collections;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.SBMTestHardware;
import org.firstinspires.ftc.teamcode.SBMTestDriveCode;

public class SBM {
	public class SBMEntry {
		public HardwareDevice device;
		public double finishTime;
		public float resetState;

		public SBMEntry(HardwareDevice device, double finishTime, float resetState ){
			this.device = device;
			this.finishTime = finishTime;
			this.resetState = resetState;
		}

		@Override
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(theRobot.myOpMode.hardwareMap.getNamesOf(device).stream().findFirst().orElse("Unknown"));
			sb.append(", ");
			sb.append(finishTime);
			sb.append(", ");
			sb.append(resetState);
			sb.append("]");
			return(sb.toString());
		}
	}
	
	// Original testing of the SBM showed that there was a roughly 1 second delay in the expected stop time and the actual stop time. This latency variable allows us to circumvent that.
	private double latency = 1000;

	public ArrayList<SBMEntry> SBMEntries = new ArrayList<SBMEntry>();
	
	public int trackedAddSBM;
	
	private SBMTestHardware theRobot = null;
	
	public SBM(SBMTestHardware hardware) {
		theRobot = hardware;
	} 

	//Used by other files to add objects and their timers to the SBM checklist
	public boolean addSBM(HardwareDevice object, double endTime, float state, double startTime) {
		endTime+=startTime;
		endTime-=latency;
		for (SBMEntry entry : SBMEntries) {
			if (entry.device == object) {
				SBMEntries.remove(entry);
				return true;
			}
		}
		SBMEntry newEntry = new SBMEntry(object, endTime, state);
		SBMEntries.add(newEntry);
		trackedAddSBM++;
		return false;
	}
	
	public void checkSBM(double runtime) {
		//go through all objects in sbm
		ArrayList<SBMEntry> toRemove = new ArrayList<SBMEntry>();
		for (SBMEntry entry : SBMEntries){
			if (entry.finishTime < runtime){
				if (entry.device instanceof DcMotor) {
					DcMotor temp = (DcMotor) entry.device;
					temp.setPower(entry.resetState);
				} else if (entry.device instanceof Servo) {
					Servo temp = (Servo) entry.device;
					temp.setPosition(entry.resetState);
				}
				toRemove.add(entry);
			}
		}
		SBMEntries.removeAll(toRemove);
	}

	public ArrayList<SBMEntry> getEntries(){
		return SBMEntries;
	}
}

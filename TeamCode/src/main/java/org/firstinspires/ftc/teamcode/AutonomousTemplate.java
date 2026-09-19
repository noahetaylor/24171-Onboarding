package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.RobotHardware;

/**
 * Minimal autonomous skeleton — no path-following library, just a scripted
 * sequence using the same Drivetrain that TeleOpTemplate uses. This is a
 * good starting point before learning Pedro Pathing later on the team repo:
 * same subsystem pattern, same motor math, just driven by fixed timing
 * instead of a closed-loop path follower.
 *
 * LinearOpMode runs top-to-bottom, once, which makes it the easiest style
 * to read and write when you're new — compare this to TeleOpTemplate's
 * init()/loop() structure, which runs continuously instead.
 */
@Autonomous(name = "Autonomous Template")
public class AutonomousTemplate extends LinearOpMode {

    private RobotHardware hardware;
    private Drivetrain drivetrain;

    @Override
    public void runOpMode() {
        hardware = new RobotHardware();
        hardware.init(hardwareMap);
        drivetrain = new Drivetrain(hardware);

        telemetry.addLine("Init complete. Waiting for start.");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            // Example sequence: drive forward for 1 second, then stop.
            // Replace with whatever your actual autonomous needs to do —
            // add more driveRobotCentric() + sleep() pairs in order.
            drivetrain.driveRobotCentric(1, 0, 0);
            sleep(1000);
            drivetrain.stop();

            telemetry.addLine("Autonomous sequence complete.");
            telemetry.update();
        }
    }
}
